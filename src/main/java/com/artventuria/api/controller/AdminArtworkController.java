package com.artventuria.api.controller;

import com.artventuria.api.domain.mongodb.ArtworkMetadata;
import com.artventuria.api.domain.postgresql.Artwork;
import com.artventuria.api.dto.artwork.ArtworkDTO;
import com.artventuria.api.dto.artwork.ArtworkPatchDTO;
import com.artventuria.api.mapper.ArtworkMapper;
import com.artventuria.api.service.artwork.ArtworkMetadataService;
import com.artventuria.api.service.artwork.ArtworkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/admin/artworks")
public class AdminArtworkController {

    private final ArtworkService artworkService;
    private final ArtworkMetadataService artworkMetadataService;
    private final ArtworkMapper artworkMapper;

    @Autowired
    public AdminArtworkController(final ArtworkService artworkService,
            final ArtworkMetadataService artworkMetadataService,
            final ArtworkMapper artworkMapper) {
        this.artworkService = artworkService;
        this.artworkMetadataService = artworkMetadataService;
        this.artworkMapper = artworkMapper;
    }

    @PostMapping
    public ResponseEntity<ArtworkDTO> createArtwork(@RequestBody final ArtworkDTO artworkDTO) {
        // Create the artwork in PostgreSQL
        final Artwork artwork = artworkMapper.toEntity(artworkDTO);
        final Artwork createdArtwork = artworkService.createArtwork(artwork);

        // Create metadata in MongoDB if it exists in the DTO
        if (artworkDTO.getMetadata() != null) {
            ArtworkMetadata metadata = new ArtworkMetadata();
            metadata.setArtworkId(createdArtwork.getId().longValue());
            metadata.setDescriptionExtended(artworkDTO.getMetadata().getDescription_extended());
            metadata.setHistoricalContext(artworkDTO.getMetadata().getHistorical_context());
            metadata.setMaterials(artworkDTO.getMetadata().getMaterials());
            metadata.setTags(artworkDTO.getMetadata().getTags());
            metadata.setExternalLinks(artworkDTO.getMetadata().getExternal_links());
            metadata.setImageUrl(artworkDTO.getMetadata().getImage_url());
            metadata.setAdditionalDetails(artworkDTO.getMetadata().getAdditional_details());

            if (artworkDTO.getMetadata().getDimensions() != null) {
                ArtworkMetadata.Dimensions dimensions = new ArtworkMetadata.Dimensions();
                dimensions.setHeight(artworkDTO.getMetadata().getDimensions().getHeight());
                dimensions.setWidth(artworkDTO.getMetadata().getDimensions().getWidth());
                dimensions.setDepth(artworkDTO.getMetadata().getDimensions().getDepth());
                dimensions.setUnit(artworkDTO.getMetadata().getDimensions().getUnit());
                metadata.setDimensions(dimensions);
            }

            artworkMetadataService.createOrUpdateMetadata(metadata);
        }

        // Return the created artwork with its metadata
        return ResponseEntity.ok(artworkMapper.toDto(createdArtwork));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtworkDTO> updateArtwork(@PathVariable final Integer id, @RequestBody final ArtworkDTO artworkDTO) {
        // Verify the artwork exists (will throw exception if not found)
        artworkService.getArtworkById(id);

        // Map DTO to entity while preserving the ID
        final Artwork artwork = artworkMapper.toEntity(artworkDTO);
        artwork.setId(id);

        // Update the artwork in PostgreSQL
        artworkService.updateArtwork(id, artwork);

        // Update metadata in MongoDB if it exists in the DTO
        if (artworkDTO.getMetadata() != null) {
            ArtworkMetadata metadata = new ArtworkMetadata();
            metadata.setArtworkId(id.longValue());
            metadata.setDescriptionExtended(artworkDTO.getMetadata().getDescription_extended());
            metadata.setHistoricalContext(artworkDTO.getMetadata().getHistorical_context());
            metadata.setMaterials(artworkDTO.getMetadata().getMaterials());
            metadata.setTags(artworkDTO.getMetadata().getTags());
            metadata.setExternalLinks(artworkDTO.getMetadata().getExternal_links());
            metadata.setImageUrl(artworkDTO.getMetadata().getImage_url());
            metadata.setAdditionalDetails(artworkDTO.getMetadata().getAdditional_details());
            metadata.setTemporaryExhibition(artworkDTO.getMetadata().isTemporary_exhibition());

            if (artworkDTO.getMetadata().getDimensions() != null) {
                ArtworkMetadata.Dimensions dimensions = new ArtworkMetadata.Dimensions();
                dimensions.setHeight(artworkDTO.getMetadata().getDimensions().getHeight());
                dimensions.setWidth(artworkDTO.getMetadata().getDimensions().getWidth());
                dimensions.setDepth(artworkDTO.getMetadata().getDimensions().getDepth());
                dimensions.setUnit(artworkDTO.getMetadata().getDimensions().getUnit());
                metadata.setDimensions(dimensions);
            }

            artworkMetadataService.createOrUpdateMetadata(metadata);
        }

        // Return the updated artwork
        Artwork updatedArtwork = artworkService.getArtworkById(id);
        return ResponseEntity.ok(artworkMapper.toDto(updatedArtwork));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ArtworkDTO> patchArtwork(
            @PathVariable final Integer id,
            @RequestBody final ArtworkPatchDTO patchDTO) {
        // Create a partial artwork object to hold the changes
        Artwork partialArtwork = new Artwork();
        
        // Apply only the fields that are present in the patch request
        patchDTO.getTitle().ifPresent(partialArtwork::setTitle);
        patchDTO.getArtist().ifPresent(partialArtwork::setArtist);
        patchDTO.getDescription().ifPresent(partialArtwork::setDescription);
        patchDTO.getCreationDate().ifPresent(partialArtwork::setCreationDate);
        patchDTO.getRarityPoints().ifPresent(partialArtwork::setRarityPoints);
        
        // Handle status field with validation
        // The validation and boolean conversion is now handled in ArtworkPatchDTO.setStatus()
        patchDTO.getStatus().ifPresent(partialArtwork::setStatus);
        
        patchDTO.getLocation().ifPresent(partialArtwork::setLocation);
        patchDTO.getVenueId().ifPresent(partialArtwork::setVenueId);
        patchDTO.getNfcTagId().ifPresent(partialArtwork::setNfcTagId);
        
        // Apply metadata fields if present
        patchDTO.getDescriptionExtended().ifPresent(partialArtwork::setDescriptionExtended);
        patchDTO.getHistoricalContext().ifPresent(partialArtwork::setHistoricalContext);
        patchDTO.getMaterials().ifPresent(partialArtwork::setMaterials);
        patchDTO.getDimensions().ifPresent(partialArtwork::setDimensions);
        patchDTO.getTags().ifPresent(partialArtwork::setTags);
        
        // Update the artwork with the partial changes
        artworkService.patchArtwork(id, partialArtwork);
        
        // Update metadata in MongoDB if it exists in the patch request
        patchDTO.getMetadata().ifPresent(metadataDTO -> {
            // Get existing metadata or create new one
            final ArtworkMetadata metadata = artworkMetadataService.getMetadataByArtworkId(id.longValue())
                    .orElseGet(() -> {
                        final ArtworkMetadata newMetadata = new ArtworkMetadata();
                        newMetadata.setArtworkId(id.longValue());
                        return newMetadata;
                    });
            
            // Only update fields that are present in the metadata DTO
            if (metadataDTO.getDescription_extended() != null) {
                metadata.setDescriptionExtended(metadataDTO.getDescription_extended());
            }
            if (metadataDTO.getHistorical_context() != null) {
                metadata.setHistoricalContext(metadataDTO.getHistorical_context());
            }
            if (metadataDTO.getMaterials() != null) {
                metadata.setMaterials(metadataDTO.getMaterials());
            }
            if (metadataDTO.getTags() != null) {
                metadata.setTags(metadataDTO.getTags());
            }
            if (metadataDTO.getExternal_links() != null) {
                metadata.setExternalLinks(metadataDTO.getExternal_links());
            }
            if (metadataDTO.getImage_url() != null) {
                metadata.setImageUrl(metadataDTO.getImage_url());
            }
            if (metadataDTO.getAdditional_details() != null) {
                metadata.setAdditionalDetails(metadataDTO.getAdditional_details());
            }
            
            // Handle temporary_exhibition field
            metadata.setTemporaryExhibition(metadataDTO.isTemporary_exhibition());
            
            // Handle dimensions if present
            if (metadataDTO.getDimensions() != null) {
                ArtworkMetadata.Dimensions dimensions = metadata.getDimensions();
                if (dimensions == null) {
                    dimensions = new ArtworkMetadata.Dimensions();
                }
                if (metadataDTO.getDimensions().getHeight() != null) {
                    dimensions.setHeight(metadataDTO.getDimensions().getHeight());
                }
                if (metadataDTO.getDimensions().getWidth() != null) {
                    dimensions.setWidth(metadataDTO.getDimensions().getWidth());
                }
                if (metadataDTO.getDimensions().getDepth() != null) {
                    dimensions.setDepth(metadataDTO.getDimensions().getDepth());
                }
                if (metadataDTO.getDimensions().getUnit() != null) {
                    dimensions.setUnit(metadataDTO.getDimensions().getUnit());
                }
                metadata.setDimensions(dimensions);
            }
            
            // Save the updated metadata
            artworkMetadataService.createOrUpdateMetadata(metadata);
        });
        
        // Return the updated artwork
        Artwork updatedArtwork = artworkService.getArtworkById(id);
        return ResponseEntity.ok(artworkMapper.toDto(updatedArtwork));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtwork(@PathVariable final Integer id) {
        // Verify the artwork exists (will throw exception if not found)
        artworkService.getArtworkById(id);

        // Delete the metadata from MongoDB if it exists
        artworkMetadataService.deleteMetadataByArtworkId(id.longValue());

        // Delete the artwork from PostgreSQL
        artworkService.deleteArtwork(id);

        return ResponseEntity.noContent().build();
    }
}