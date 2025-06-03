package com.artventuria.api.dto.auth;

import java.time.Instant;

public class ExtendedUserResponse extends UserResponse {
    private Instant lastLogin;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer badgeCount;
    private Integer artworkCount;

    public ExtendedUserResponse() {
        super();
    }

    public ExtendedUserResponse(Integer id, String username, String email, Integer points,
            Instant lastLogin, Instant createdAt, Instant updatedAt, Integer badgeCount, Integer artworkCount) {
        super(id, username, email, points);
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.badgeCount = badgeCount;
        this.artworkCount = artworkCount;
    }


    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(Integer badgeCount) {
        this.badgeCount = badgeCount;
    }

    public Integer getArtworkCount() {
        return artworkCount;
    }

    public void setArtworkCount(Integer artworkCount) {
        this.artworkCount = artworkCount;
    }
}