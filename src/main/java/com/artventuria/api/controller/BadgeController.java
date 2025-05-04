package com.artventuria.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.domain.postgresql.BadgeProgress;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.service.badge.BadgeProgressService;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeProgressService badgeProgressService;

    public BadgeController(BadgeProgressService badgeProgressService) {
        this.badgeProgressService = badgeProgressService;
    }

    @GetMapping
    public ResponseEntity<List<Badge>> getAllBadges() {
        throw new ResourceNotFoundException("Badges", "feature", "not yet implemented");
    }

    @GetMapping("/{badgeId}")
    public ResponseEntity<Badge> getBadgeById(@PathVariable Integer badgeId) {
        throw new ResourceNotFoundException("Badge", "id", badgeId);
    }

    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<Badge>> getCompletedBadges(@PathVariable Integer userId) {
        return ResponseEntity.ok(badgeProgressService.getCompletedBadges(userId));
    }

    @GetMapping("/user/{userId}/progress")
    public ResponseEntity<List<BadgeProgress>> getUserBadgeProgress(@PathVariable Integer userId) {
        return ResponseEntity.ok(badgeProgressService.getUserBadgeProgresses(userId));
    }

    @GetMapping("/user/{userId}/badge/{badgeId}/progress")
    public ResponseEntity<BadgeProgress> getBadgeProgress(
            @PathVariable Integer userId,
            @PathVariable Integer badgeId) {
        return ResponseEntity.ok(badgeProgressService.getBadgeProgress(userId, badgeId));
    }

}

    