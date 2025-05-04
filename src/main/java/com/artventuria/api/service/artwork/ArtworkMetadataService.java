package com.artventuria.api.service.artwork;

import com.artventuria.api.domain.mongodb.ArtworkMetadata;
import com.artventuria.api.dto.artwork.ArtworkMetadataDTO;
import java.util.Optional;

public interface ArtworkMetadataService {
    /**
     * Retrieves artwork metadata from MongoDB by artwork ID
     * 
     * @param artworkId The ID of the artwork
     * @return Optional containing the artwork metadata if found
     */
    Optional<ArtworkMetadata> getMetadataByArtworkId(Long artworkId);
    
    /**
     * Converts MongoDB ArtworkMetadata to DTO
     * 
     * @param metadata The MongoDB metadata entity
     * @return The DTO representation of the metadata
     */
    ArtworkMetadataDTO convertToDto(ArtworkMetadata metadata);

    /**
     * Creates or updates artwork metadata in MongoDB
     * 
     * @param metadata The artwork metadata to create or update
     * @return The created or updated artwork metadata
     */
    ArtworkMetadata createOrUpdateMetadata(ArtworkMetadata metadata);

    /**
     * Deletes artwork metadata from MongoDB by artwork ID
     * 
     * @param artworkId The ID of the artwork whose metadata should be deleted
     */
    void deleteMetadataByArtworkId(Long artworkId);
}