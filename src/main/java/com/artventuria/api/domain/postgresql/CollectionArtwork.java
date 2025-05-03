package com.artventuria.api.domain.postgresql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "collection_artworks")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CollectionArtworkId.class)
public class CollectionArtwork {
    @Id
    @Column(name = "collection_id")
    private Integer collectionId;

    @Id
    @Column(name = "artwork_id")
    private Integer artworkId;

    @Column(name = "acquisition_date")
    private Instant acquisitionDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", insertable = false, updatable = false)
    private Collection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id", insertable = false, updatable = false)
    private Artwork artwork;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (acquisitionDate == null) {
            acquisitionDate = Instant.now();
        }
    }
}
