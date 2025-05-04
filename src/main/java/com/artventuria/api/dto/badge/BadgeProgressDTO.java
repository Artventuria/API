package com.artventuria.api.dto.badge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeProgressDTO {
    private Integer id;
    private Integer userId;
    private Integer badgeId;
    private Integer progress;
    private Boolean completed;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    
    // Badge details
    private String badgeName;
    private String badgeDescription;
    private String badgeImageUrl;
    private String badgeCriteria;
    private Integer badgePoints;
}