package com.artventuria.api.dto.badge;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeProgressResponse {
    private Long badgeId;
    private String badgeName;
    private String imageUrl;
    private Integer currentPoints;
    private Integer pointsRequired;
    private Double progressPercentage;
    private boolean achieved;
    private String achievedDate;
}