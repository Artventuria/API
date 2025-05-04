package com.artventuria.api.dto.badge;

import lombok.Data;

@Data
public class UpdateBadgeRequest {
    private String name;
    private String description;
    private String imageUrl;
    private String criteria;
    private Integer points;
}