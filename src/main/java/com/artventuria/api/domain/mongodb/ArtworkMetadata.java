package com.artventuria.api.domain.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

@Document(collection = "artwork_metadata")
public class ArtworkMetadata {
    @Id
    private String id;

    @Field("artwork_id")
    @NotNull
    @Min(1)
    @Indexed(unique = true)
    private Long artworkId;

    @Field("description_extended")
    private String descriptionExtended;

    @Field("historical_context")
    private String historicalContext;

    private List<String> materials;

    private Dimensions dimensions;

    @Indexed
    private List<String> tags;

    @Field("external_links")
    private List<String> externalLinks;
    
    @Field("image_url")
    private String imageUrl;

    @Field("temporary_exhibition")
    private boolean temporaryExhibition;

    @Field("additional_details")
    private Object additionalDetails;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;

    // Nested class for dimensions
    public static class Dimensions {
        private Double height;
        private Double width;
        private Double depth;
        private String unit;

        public Double getHeight() {
            return height;
        }

        public void setHeight(Double height) {
            this.height = height;
        }

        public Double getWidth() {
            return width;
        }

        public void setWidth(Double width) {
            this.width = width;
        }

        public Double getDepth() {
            return depth;
        }

        public void setDepth(Double depth) {
            this.depth = depth;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getArtworkId() {
        return artworkId;
    }

    public void setArtworkId(Long artworkId) {
        this.artworkId = artworkId;
    }

    public String getDescriptionExtended() {
        return descriptionExtended;
    }

    public void setDescriptionExtended(String descriptionExtended) {
        this.descriptionExtended = descriptionExtended;
    }

    public String getHistoricalContext() {
        return historicalContext;
    }

    public void setHistoricalContext(String historicalContext) {
        this.historicalContext = historicalContext;
    }

    public List<String> getMaterials() {
        return materials;
    }

    public void setMaterials(List<String> materials) {
        this.materials = materials;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isTemporaryExhibition() {
        return temporaryExhibition;
    }

    public void setTemporaryExhibition(boolean temporaryExhibition) {
        this.temporaryExhibition = temporaryExhibition;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(List<String> externalLinks) {
        this.externalLinks = externalLinks;
    }

    public Object getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(Object additionalDetails) {
        this.additionalDetails = additionalDetails;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}