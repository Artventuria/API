package com.artventuria.api.domain.postgresql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "artworks")

@NoArgsConstructor
@AllArgsConstructor
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, name = "creation_date")
    private Instant creationDate;

    @Column(name = "rarity_points", nullable = false)
    private Integer rarityPoints;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String location;

    @Column(name = "venue_id")
    private String venueId;

    @Column(name = "nfc_tag_id")
    private Integer nfcTagId;

    @Transient
    private String descriptionExtended;

    @Transient
    private String historicalContext;

    @Transient
    private String materials;

    @Transient
    private String dimensions;

    @Transient
    private List<String> tags;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}