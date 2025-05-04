package com.artventuria.api.dto.badge;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
public class CreateBadgeRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    @JsonProperty("image_url")
    private String imageUrl;

    @NotNull
    private Integer points;

    @NotBlank
    private String type;

    // Contains only the requirements (without the type)
    private Map<String, Object> criteria;
}