package com.artventuria.api.service.artwork.impl;

import com.artventuria.api.domain.postgresql.Artwork;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.repository.jpa.artwork.ArtworkRepository;
import com.artventuria.api.service.artwork.ArtworkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArtworkServiceImpl implements ArtworkService {

    private final ArtworkRepository artworkRepository;

    @Autowired
    public ArtworkServiceImpl(ArtworkRepository artworkRepository) {
        this.artworkRepository = artworkRepository;
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
        return artworkRepository.searchByFullText(query, PageRequest.of(offset / limit, limit));
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
}