package com.artventuria.api.service.artwork.impl;

import com.artventuria.api.domain.mongodb.ScanLog;
import com.artventuria.api.domain.postgresql.Artwork;
import com.artventuria.api.dto.artwork.ArtworkDTO;
import com.artventuria.api.dto.artwork.StillToCollectResponse;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.mapper.ArtworkMapper;
import com.artventuria.api.repository.jpa.artwork.ArtworkRepository;
import com.artventuria.api.repository.mongo.ScanLogRepository;
import com.artventuria.api.service.artwork.ArtworkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArtworkServiceImpl implements ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ScanLogRepository scanLogRepository;
    private final ArtworkMapper artworkMapper;

    @Autowired
    public ArtworkServiceImpl(
            ArtworkRepository artworkRepository,
            ScanLogRepository scanLogRepository,
            ArtworkMapper artworkMapper) {
        this.artworkRepository = artworkRepository;
        this.scanLogRepository = scanLogRepository;
        this.artworkMapper = artworkMapper;
    }

    @Override
    public Artwork createArtwork(Artwork artwork) {
        return artworkRepository.save(artwork);
    }

    @Override
    public Artwork getArtworkById(Integer artworkId) {
        return artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ResourceNotFoundException("Artwork not found with id: " + artworkId));
    }

    @Override
    public List<Artwork> getUserArtworks(String artist, int limit, int offset) {
        if (artist == null) {
            return artworkRepository.findAll(PageRequest.of(offset / limit, limit)).getContent();
        }
        return artworkRepository.findByArtist(artist);
    }

    @Override
    public List<Artwork> searchArtworks(String query, int limit, int offset) {
        if (query == null || query.trim().isEmpty()) {
            return artworkRepository.findAll(PageRequest.of(offset / limit, limit)).getContent();
        }
        // Generate a tsquery with prefix on each word
        String tsquery = java.util.Arrays.stream(query.trim().split("\\s+")).map(s -> s + ":*")
                .reduce((a, b) -> a + " & " + b).orElse("");
        String likequery = query.trim();
        return artworkRepository.searchUserFriendly(tsquery, likequery, PageRequest.of(offset / limit, limit));
    }

    @Override
    public void updateArtwork(Integer artworkId, Artwork artwork) {
        Artwork existingArtwork = getArtworkById(artworkId);
        // Update all artwork properties
        existingArtwork.setTitle(artwork.getTitle());
        existingArtwork.setDescription(artwork.getDescription());
        existingArtwork.setDescriptionExtended(artwork.getDescriptionExtended());
        existingArtwork.setHistoricalContext(artwork.getHistoricalContext());
        existingArtwork.setMaterials(artwork.getMaterials());
        existingArtwork.setDimensions(artwork.getDimensions());
        existingArtwork.setTags(artwork.getTags());

        existingArtwork.setLocation(artwork.getLocation());
        existingArtwork.setVenueId(artwork.getVenueId());
        artworkRepository.save(existingArtwork);
    }

    @Override
    public void deleteArtwork(Integer artworkId) {
        artworkRepository.deleteById(artworkId);
    }

    @Override
    public void patchArtwork(Integer artworkId, Artwork partialArtwork) {
        Artwork existingArtwork = getArtworkById(artworkId);

        // Only update fields that are not null in the partial artwork
        if (partialArtwork.getTitle() != null) {
            existingArtwork.setTitle(partialArtwork.getTitle());
        }
        if (partialArtwork.getArtist() != null) {
            existingArtwork.setArtist(partialArtwork.getArtist());
        }
        if (partialArtwork.getDescription() != null) {
            existingArtwork.setDescription(partialArtwork.getDescription());
        }
        if (partialArtwork.getCreationDate() != null) {
            existingArtwork.setCreationDate(partialArtwork.getCreationDate());
        }
        if (partialArtwork.getRarityPoints() != null) {
            existingArtwork.setRarityPoints(partialArtwork.getRarityPoints());
        }
        if (partialArtwork.getStatus() != null) {
            existingArtwork.setStatus(partialArtwork.getStatus());
        }
        if (partialArtwork.getLocation() != null) {
            existingArtwork.setLocation(partialArtwork.getLocation());
        }
        if (partialArtwork.getVenueId() != null) {
            existingArtwork.setVenueId(partialArtwork.getVenueId());
        }
        if (partialArtwork.getNfcTagId() != null) {
            existingArtwork.setNfcTagId(partialArtwork.getNfcTagId());
        }

        // Update transient fields for metadata if provided
        if (partialArtwork.getDescriptionExtended() != null) {
            existingArtwork.setDescriptionExtended(partialArtwork.getDescriptionExtended());
        }
        if (partialArtwork.getHistoricalContext() != null) {
            existingArtwork.setHistoricalContext(partialArtwork.getHistoricalContext());
        }
        if (partialArtwork.getMaterials() != null) {
            existingArtwork.setMaterials(partialArtwork.getMaterials());
        }
        if (partialArtwork.getDimensions() != null) {
            existingArtwork.setDimensions(partialArtwork.getDimensions());
        }
        if (partialArtwork.getTags() != null) {
            existingArtwork.setTags(partialArtwork.getTags());
        }

        artworkRepository.save(existingArtwork);
    }

    @Override
    public List<StillToCollectResponse> getStillToCollectArtworks(Integer userId, int limit, String cursor) {
        // Decode cursor if it exists
        Integer lastId = null;
        if (cursor != null && !cursor.isEmpty()) {
            try {
                String decodedCursor = new String(Base64.getDecoder().decode(cursor));
                lastId = Integer.parseInt(decodedCursor);
            } catch (Exception e) {
                // Ignore cursor on decode error
                lastId = null;
            }
        }

        // Get IDs of artworks already scanned by the user
        List<ScanLog> scannedLogs = scanLogRepository.findValidArtworkScansByUserId(userId.longValue());
        List<Long> scannedArtworkIds = scannedLogs.stream()
                .map(ScanLog::getArtworkId)
                .distinct()
                .collect(Collectors.toList());

        // Check if user has already scanned all artworks
        Long totalArtworks = artworkRepository.count();
        if (scannedArtworkIds.size() >= totalArtworks) {
            // User has scanned all artworks
            return Collections.emptyList();
        }

        // Get random artworks not scanned by the user
        List<Artwork> artworks = artworkRepository.findRandomArtworksNotScannedByUser(
                scannedArtworkIds,
                scannedArtworkIds.isEmpty(),
                lastId,
                limit);

        if (artworks.isEmpty()) {
            return Collections.emptyList();
        }

        // Prepare cursor for next page
        Integer nextLastId = artworks.stream()
                .map(Artwork::getId)
                .max(Integer::compare)
                .orElse(null);

        String nextPageCursor = null;
        if (nextLastId != null) {
            nextPageCursor = Base64.getEncoder().encodeToString(nextLastId.toString().getBytes());
        }

        // Convert artworks to complete responses with all metadata
        List<StillToCollectResponse> responses = new ArrayList<>();
        for (Artwork artwork : artworks) {
            // Use mapper to create a complete DTO with all metadata
            ArtworkDTO artworkDTO = artworkMapper.toDto(artwork);

            // Create response by copying all properties from DTO and adding cursor
            StillToCollectResponse response = new StillToCollectResponse(artworkDTO);

            // Add cursor only to the last response
            if (artwork.getId().equals(nextLastId)) {
                response.setNextPageCursor(nextPageCursor);
            }

            responses.add(response);
        }

        return responses;
    }
}