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

@Service
@Transactional
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final CollectionArtworkRepository collectionArtworkRepository;

    @Autowired
    public CollectionServiceImpl(
            CollectionRepository collectionRepository,
            CollectionArtworkRepository collectionArtworkRepository) {
        this.collectionRepository = collectionRepository;
        this.collectionArtworkRepository = collectionArtworkRepository;

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
}