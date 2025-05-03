package com.artventuria.api.domain.postgresql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionArtworkId implements Serializable {
    private Integer collectionId;
    private Integer artworkId;
}
