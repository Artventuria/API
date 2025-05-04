package com.artventuria.api.dto.scan;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class ScanHistoryResponse {
    private String id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("artwork_id")
    private Long artworkId;

    private Integer points;

    private String type;

    @JsonProperty("created_at")
    private Instant createdAt;

    public ScanHistoryResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArtworkId() {
        return artworkId;
    }

    public void setArtworkId(Long artworkId) {
        this.artworkId = artworkId;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
