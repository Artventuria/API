package com.artventuria.api.mapper;

import com.artventuria.api.domain.postgresql.Artwork;
import com.artventuria.api.dto.artwork.ArtworkDTO;
import com.artventuria.api.dto.artwork.ArtworkMetadataDTO;
import com.artventuria.api.service.artwork.ArtworkMetadataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

@Component
public class ArtworkMapper {

    private final ArtworkMetadataService artworkMetadataService;

    @Autowired
    public ArtworkMapper(ArtworkMetadataService artworkMetadataService) {
        this.artworkMetadataService = artworkMetadataService;
    }

    public ArtworkDTO toDto(Artwork artwork) {
        if (artwork == null) {
            return null;
        }

        ArtworkDTO dto = new ArtworkDTO();
        dto.setId(artwork.getId());
        dto.setTitle(artwork.getTitle());
        dto.setArtist(artwork.getArtist());
        dto.setDescription(artwork.getDescription());
        dto.setCreationDate(artwork.getCreationDate());
        dto.setRarityPoints(artwork.getRarityPoints());
        dto.setStatus(artwork.getStatus());
        dto.setLocation(artwork.getLocation());
        dto.setVenueId(artwork.getVenueId());
        dto.setNfcTagId(artwork.getNfcTagId());
        dto.setCreatedAt(artwork.getCreatedAt());
        dto.setUpdatedAt(artwork.getUpdatedAt());

        // Store transient fields for metadata processing
        dto.setDescriptionExtended(artwork.getDescriptionExtended());
        dto.setHistoricalContext(artwork.getHistoricalContext());
        dto.setMaterials(artwork.getMaterials());
        dto.setDimensions(artwork.getDimensions());
        dto.setTags(artwork.getTags());

        // Retrieve metadata from MongoDB
        ArtworkMetadataDTO metadata = artworkMetadataService
                .getMetadataByArtworkId(artwork.getId().longValue())
                .map(artworkMetadataService::convertToDto)
                .orElseGet(() -> {
                    // If no metadata found in MongoDB, create an empty metadata object
                    ArtworkMetadataDTO emptyMetadata = new ArtworkMetadataDTO();
                    emptyMetadata.setId(artwork.getId().toString());
                    emptyMetadata.setArtwork_id(artwork.getId());
                    emptyMetadata.setMaterials(new ArrayList<>());
                    emptyMetadata.setTags(new ArrayList<>());
                    emptyMetadata.setExternal_links(new ArrayList<>());
                    emptyMetadata.setCreated_at(artwork.getCreatedAt());
                    emptyMetadata.setUpdated_at(artwork.getUpdatedAt());
                    return emptyMetadata;
                });

        dto.setMetadata(metadata);

        return dto;
    }

    public Artwork toEntity(ArtworkDTO dto) {
        if (dto == null) {
            return null;
        }

        Artwork artwork = new Artwork();
        artwork.setId(dto.getId());
        artwork.setTitle(dto.getTitle());
        artwork.setArtist(dto.getArtist());
        artwork.setDescription(dto.getDescription());
        artwork.setDescriptionExtended(dto.getDescriptionExtended());
        artwork.setHistoricalContext(dto.getHistoricalContext());
        artwork.setMaterials(dto.getMaterials());
        artwork.setDimensions(dto.getDimensions());
        artwork.setTags(dto.getTags());
        artwork.setCreationDate(dto.getCreationDate());
        artwork.setRarityPoints(dto.getRarityPoints());
        artwork.setStatus(dto.getStatus());
        artwork.setLocation(dto.getLocation());
        artwork.setVenueId(dto.getVenueId());
        artwork.setNfcTagId(dto.getNfcTagId());
        artwork.setCreatedAt(dto.getCreatedAt());
        artwork.setUpdatedAt(dto.getUpdatedAt());

        return artwork;
    }
}