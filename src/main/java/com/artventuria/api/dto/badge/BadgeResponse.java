package com.artventuria.api.dto.badge;

import com.artventuria.api.domain.postgresql.Badge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.LinkedHashMap;

@Data
public class BadgeResponse {
    private Integer id;
    private String name;
    private String description;

    @JsonProperty("image_url")
    private String imageUrl;

    private Integer points;
    private String type;
    private Map<String, Object> criteria;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    public static BadgeResponse fromBadge(Badge badge) {
        BadgeResponse response = new BadgeResponse();
        response.setId(badge.getId());
        response.setName(badge.getName());
        response.setDescription(badge.getDescription());
        response.setImageUrl(badge.getImageUrl());
        response.setPoints(badge.getPoints());
        response.setCreatedAt(badge.getCreatedAt());
        response.setUpdatedAt(badge.getUpdatedAt());
        // Extract the type and criteria from the JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Use LinkedHashMap to preserve the order of the fields
            Map<String, Object> criteriaMap = objectMapper.readValue(badge.getCriteria(),
                    new TypeReference<LinkedHashMap<String, Object>>() {
                    });

            response.setType((String) criteriaMap.get("type"));
            @SuppressWarnings("unchecked")
            Map<String, Object> requirements = (Map<String, Object>) criteriaMap.get("requirements");
            response.setCriteria(requirements);
        } catch (Exception e) {
            // If an error occurs, leave the type and criteria fields empty
        }

        return response;
    }
}