package com.artventuria.api.dto.artwork;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkDTO {
    private Integer id;
    
    @NotNull
    private String title;
    
    @NotNull
    private String artist;
    
    @NotNull
    private String description;
    
    @NotNull
    @JsonProperty("creation_date")
    private Instant creationDate;
    
    @NotNull
    @JsonProperty("rarity_points")
    private Integer rarityPoints;
    
    @NotNull
    private String status;
    
    @NotNull
    private String location;
    
    @JsonProperty("venue_id")
    private String venueId;
    
    @JsonProperty("nfc_tag_id")
    private Integer nfcTagId;
    
    private ArtworkMetadataDTO metadata;
    
    @JsonProperty("created_at")
    private Instant createdAt;
    
    @JsonProperty("updated_at")
    private Instant updatedAt;
    
    // Transient fields not included in the response directly but used to populate metadata
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String descriptionExtended;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String historicalContext;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String materials;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String dimensions;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> tags;
}