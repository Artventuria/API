package com.artventuria.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.domain.postgresql.BadgeProgress;
import com.artventuria.api.dto.badge.BadgeDTO;
import com.artventuria.api.dto.badge.BadgeProgressDTO;
import com.artventuria.api.service.badge.BadgeProgressService;
import com.artventuria.api.service.badge.BadgeService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeProgressService badgeProgressService;
    private final BadgeService badgeService;

    public BadgeController(BadgeProgressService badgeProgressService, BadgeService badgeService) {
        this.badgeProgressService = badgeProgressService;
        this.badgeService = badgeService;
    }
    
    // Helper method to convert Badge to BadgeDTO
    private BadgeDTO convertToDTO(Badge badge) {
        return new BadgeDTO(
            badge.getId(),
            badge.getName(),
            badge.getDescription(),
            badge.getImageUrl(),
            badge.getCriteria(),
            badge.getPoints(),
            badge.getCreatedAt(),
            badge.getUpdatedAt()
        );
    }
    
    // Helper method to convert BadgeProgress to BadgeProgressDTO
    private BadgeProgressDTO convertToDTO(BadgeProgress progress) {
        Badge badge = progress.getBadge();
        return new BadgeProgressDTO(
            progress.getId(),
            progress.getUserId(),
            progress.getBadgeId(),
            progress.getProgress(),
            progress.getCompleted(),
            progress.getCreatedAt(),
            progress.getUpdatedAt(),
            badge.getName(),
            badge.getDescription(),
            badge.getImageUrl(),
            badge.getCriteria(),
            badge.getPoints()
        );
    }

    @GetMapping
    public ResponseEntity<List<BadgeDTO>> getAllBadges() {
        List<Badge> badges = badgeService.getAllBadges();
        List<BadgeDTO> badgeDTOs = badges.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(badgeDTOs);
    }

    @GetMapping("/{badgeId}")
    public ResponseEntity<BadgeDTO> getBadgeById(@PathVariable Integer badgeId) {
        Badge badge = badgeService.getBadgeById(badgeId);
        return ResponseEntity.ok(convertToDTO(badge));
    }

    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<BadgeDTO>> getCompletedBadges(@PathVariable Integer userId) {
        List<Badge> badges = badgeProgressService.getCompletedBadges(userId);
        List<BadgeDTO> badgeDTOs = badges.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(badgeDTOs);
    }

    @GetMapping("/user/{userId}/progress")
    public ResponseEntity<List<BadgeProgressDTO>> getUserBadgeProgress(@PathVariable Integer userId) {
        List<BadgeProgress> progresses = badgeProgressService.getUserBadgeProgresses(userId);
        List<BadgeProgressDTO> progressDTOs = progresses.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(progressDTOs);
    }

    @GetMapping("/user/{userId}/badge/{badgeId}/progress")
    public ResponseEntity<BadgeProgressDTO> getBadgeProgress(
            @PathVariable Integer userId,
            @PathVariable Integer badgeId) {
        BadgeProgress progress = badgeProgressService.getBadgeProgress(userId, badgeId);
        return ResponseEntity.ok(convertToDTO(progress));
    }

}

    