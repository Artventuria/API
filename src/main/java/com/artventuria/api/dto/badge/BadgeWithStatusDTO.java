package com.artventuria.api.dto.badge;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class BadgeWithStatusDTO extends BadgeDTO {
    private Boolean obtained;
    private Integer progress;
    
    public BadgeWithStatusDTO(Integer id, String name, String description, String imageUrl, 
                              String criteria, Integer points, Instant createdAt, Instant updatedAt,
                              Boolean obtained, Integer progress) {
        super(id, name, description, imageUrl, criteria, points, createdAt, updatedAt);
        this.obtained = obtained;
        this.progress = progress;
    }
}
