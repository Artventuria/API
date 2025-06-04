package com.artventuria.api.dto.artwork;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StillToCollectResponse extends ArtworkDTO {

    // A cursor for pagination, represents the ID of the last work returned
    private String nextPageCursor;

    public StillToCollectResponse(ArtworkDTO dto) {
        // Copy all properties of ArtworkDTO
        this.setId(dto.getId());
        this.setTitle(dto.getTitle());
        this.setArtist(dto.getArtist());
        this.setDescription(dto.getDescription());
        this.setCreationDate(dto.getCreationDate());
        this.setRarityPoints(dto.getRarityPoints());
        this.setStatus(dto.getStatus());
        this.setLocation(dto.getLocation());
        this.setVenueId(dto.getVenueId());
        this.setNfcTagId(dto.getNfcTagId());
        this.setCreatedAt(dto.getCreatedAt());
        this.setUpdatedAt(dto.getUpdatedAt());
        this.setMetadata(dto.getMetadata());
    }
}
