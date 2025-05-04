package com.artventuria.api.dto.nfc;

import java.time.Instant;
import com.artventuria.api.dto.artwork.ArtworkDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to represent an NFC tag with the associated artwork.
 */
@Data
@NoArgsConstructor
public class NFCTagDTO {
    private Integer id;
    private String token;
    private ArtworkDTO artwork;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;
}
