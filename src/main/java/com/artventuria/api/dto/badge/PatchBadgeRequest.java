package com.artventuria.api.dto.badge;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@Data
public class PatchBadgeRequest {
    private String name;

    private String description;

    @JsonProperty("image_url")
    private String imageUrl;

    private Integer points;

    private String type;

    // Only contains the requirements (without the type)
    private Map<String, Object> criteria;
}