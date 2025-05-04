package com.artventuria.api.dto.artwork;

import lombok.Data;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
public class ArtworkPatchDTO {

    public ArtworkPatchDTO() {
        this.title = Optional.empty();
        this.artist = Optional.empty();
        this.description = Optional.empty();
        this.creationDate = Optional.empty();
        this.rarityPoints = Optional.empty();
        this.status = Optional.empty();
        this.location = Optional.empty();
        this.venueId = Optional.empty();
        this.nfcTagId = Optional.empty();
        this.descriptionExtended = Optional.empty();
        this.historicalContext = Optional.empty();
        this.materials = Optional.empty();
        this.dimensions = Optional.empty();
        this.tags = Optional.empty();
        this.metadata = Optional.empty();
    }

    private Optional<String> title;
    private Optional<String> artist;
    private Optional<String> description;

    @JsonProperty("creation_date")
    private Optional<Instant> creationDate;

    @JsonProperty("rarity_points")
    private Optional<Integer> rarityPoints;

    @JsonProperty("status")
    private Optional<String> status;
    private Optional<String> location;

    @JsonProperty("venue_id")
    private Optional<String> venueId;

    @JsonProperty("nfc_tag_id")
    private Optional<Integer> nfcTagId;

    // Transient fields for metadata
    private Optional<String> descriptionExtended;
    private Optional<String> historicalContext;
    private Optional<String> materials;
    private Optional<String> dimensions;
    private Optional<List<String>> tags;

    // Metadata field for direct metadata updates
    private Optional<ArtworkMetadataDTO> metadata;
}