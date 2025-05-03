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
@Table(name = "leaderboards")
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "rank", nullable = false)
    private int rank;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Transient
    private String username;

    @Transient
    private int badgeCount;
}