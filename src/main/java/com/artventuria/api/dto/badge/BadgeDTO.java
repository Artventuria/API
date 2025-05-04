package com.artventuria.api.dto.badge;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    private String criteria;
    private Integer points;
    private Instant createdAt;
    private Instant updatedAt;
}