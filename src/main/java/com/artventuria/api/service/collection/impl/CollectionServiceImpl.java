package com.artventuria.api.service.collection.impl;

import com.artventuria.api.domain.postgresql.Collection;
import com.artventuria.api.domain.postgresql.Artwork;
import com.artventuria.api.domain.postgresql.CollectionArtwork;
import com.artventuria.api.repository.jpa.artwork.CollectionArtworkRepository;
import com.artventuria.api.repository.jpa.artwork.CollectionRepository;
import com.artventuria.api.service.collection.CollectionService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.artventuria.api.dto.artwork.ArtworkDTO;
import com.artventuria.api.mapper.ArtworkMapper;

@Service
@Transactional
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final CollectionArtworkRepository collectionArtworkRepository;
    private final ArtworkMapper artworkMapper;

    @Autowired
    public CollectionServiceImpl(
            CollectionRepository collectionRepository,
            CollectionArtworkRepository collectionArtworkRepository,
            ArtworkMapper artworkMapper) {
        this.collectionRepository = collectionRepository;
        this.collectionArtworkRepository = collectionArtworkRepository;
        this.artworkMapper = artworkMapper;
    }

    @Override
    public Collection getCollectionById(Integer collectionId) {
        return collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found with id: " + collectionId));
    }

    @Override
    public List<Collection> getUserCollections(Integer userId, int limit, int offset) {
        return collectionRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void addArtworkToCollection(Integer collectionId, Integer artworkId) {
        // Check if the artwork is already in the collection
        if (isArtworkInCollection(collectionId, artworkId)) {
            return; // Already in the collection, do nothing
        }

        // Create a new entry in the join table
        CollectionArtwork collectionArtwork = new CollectionArtwork();
        collectionArtwork.setCollectionId(collectionId);
        collectionArtwork.setArtworkId(artworkId);
        collectionArtwork.setAcquisitionDate(Instant.now());

        // Save the association
        collectionArtworkRepository.save(collectionArtwork);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artwork> getCollectionArtworks(Integer collectionId, int limit, int offset) {
        return collectionArtworkRepository.findByCollectionId(collectionId).stream()
                .skip(offset)
                .limit(limit)
                .map(CollectionArtwork::getArtwork)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isArtworkInCollection(Integer collectionId, Integer artworkId) {
        return collectionArtworkRepository.existsByCollectionIdAndArtworkId(collectionId, artworkId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCollectionArtworksCount(Integer collectionId) {
        return collectionArtworkRepository.findByCollectionId(collectionId).size();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ArtworkDTO> getRecentlyCollectedArtworks(Integer userId, int hoursAgo) {
        // Calculate the date from which to retrieve artworks (e.g., 168 hours = 1 week ago)
        Instant sinceDate = Instant.now().minusSeconds(hoursAgo * 3600L);
        
        // Get artworks collected since the specified date
        List<CollectionArtwork> collectionArtworks = collectionArtworkRepository.findCollectedSinceByUserId(userId, sinceDate);
        
        // Convert to DTOs using the ArtworkMapper
        return collectionArtworks.stream()
                .map(ca -> ca.getArtwork()) // Extract the Artwork entity
                .map(artwork -> artworkMapper.toDto(artwork)) // Convert to ArtworkDTO
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isArtworkInUserCollection(Integer userId, Integer artworkId) {
        // Get all collections for the user
        List<Collection> userCollections = collectionRepository.findByUserId(userId);
        
        // Check if the artwork is in any of the user's collections
        for (Collection collection : userCollections) {
            if (isArtworkInCollection(collection.getId(), artworkId)) {
                return true;
            }
        }
        
        return false;
    }
}