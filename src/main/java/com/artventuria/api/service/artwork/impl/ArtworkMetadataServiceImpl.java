package com.artventuria.api.service.artwork.impl;

import com.artventuria.api.domain.mongodb.ArtworkMetadata;
import com.artventuria.api.dto.artwork.ArtworkMetadataDTO;
import com.artventuria.api.repository.mongo.ArtworkMetadataRepository;
import com.artventuria.api.service.artwork.ArtworkMetadataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.time.Instant;

@Service
public class ArtworkMetadataServiceImpl implements ArtworkMetadataService {

    private final ArtworkMetadataRepository artworkMetadataRepository;

    @Autowired
    public ArtworkMetadataServiceImpl(ArtworkMetadataRepository artworkMetadataRepository) {
        this.artworkMetadataRepository = artworkMetadataRepository;
    }

    @Override
    public Optional<ArtworkMetadata> getMetadataByArtworkId(Long artworkId) {
        return artworkMetadataRepository.findByArtworkId(artworkId);
    }

    @Override
    public ArtworkMetadataDTO convertToDto(ArtworkMetadata metadata) {
        if (metadata == null) {
            return null;
        }

        ArtworkMetadataDTO dto = new ArtworkMetadataDTO();
        dto.setId(metadata.getId());
        dto.setArtwork_id(metadata.getArtworkId().intValue());
        dto.setDescription_extended(metadata.getDescriptionExtended());
        dto.setHistorical_context(metadata.getHistoricalContext());
        dto.setMaterials(metadata.getMaterials() != null ? metadata.getMaterials() : new ArrayList<>());

        if (metadata.getDimensions() != null) {
            ArtworkMetadataDTO.DimensionsDTO dimensionsDTO = new ArtworkMetadataDTO.DimensionsDTO(
                    metadata.getDimensions().getDepth(),
                    metadata.getDimensions().getHeight(),
                    metadata.getDimensions().getUnit(),
                    metadata.getDimensions().getWidth());
            dto.setDimensions(dimensionsDTO);
        }

        dto.setTags(metadata.getTags() != null ? metadata.getTags() : new ArrayList<>());
        dto.setExternal_links(metadata.getExternalLinks() != null ? metadata.getExternalLinks() : new ArrayList<>());
        dto.setTemporary_exhibition(metadata.isTemporaryExhibition());
        dto.setAdditional_details(metadata.getAdditionalDetails());
        dto.setCreated_at(metadata.getCreatedAt());
        dto.setUpdated_at(metadata.getUpdatedAt());

        return dto;
    }

    @Override
    public ArtworkMetadata createOrUpdateMetadata(ArtworkMetadata metadata) {
        // Set creation/update timestamps if not already set
        if (metadata.getCreatedAt() == null) {
            metadata.setCreatedAt(Instant.now());
        }
        metadata.setUpdatedAt(Instant.now());

        // Check if metadata already exists for this artwork
        Optional<ArtworkMetadata> existingMetadata = artworkMetadataRepository.findByArtworkId(metadata.getArtworkId());

        if (existingMetadata.isPresent()) {
            // If exists, preserve the ID and created date
            metadata.setId(existingMetadata.get().getId());
            metadata.setCreatedAt(existingMetadata.get().getCreatedAt());
        }

        // Save or update the metadata
        return artworkMetadataRepository.save(metadata);
    }

    @Override
    public void deleteMetadataByArtworkId(Long artworkId) {
        artworkMetadataRepository.deleteByArtworkId(artworkId);
    }
}