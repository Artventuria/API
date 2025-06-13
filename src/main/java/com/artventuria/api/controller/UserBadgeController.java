package com.artventuria.api.controller;

import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.domain.postgresql.BadgeProgress;
import com.artventuria.api.dto.badge.BadgeDTO;
import com.artventuria.api.dto.badge.BadgeProgressDTO;
import com.artventuria.api.service.badge.BadgeProgressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.artventuria.api.dto.badge.BadgeWithStatusDTO;
import com.artventuria.api.service.badge.BadgeService;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserBadgeController {

    private final BadgeProgressService badgeProgressService;
    private final BadgeService badgeService;

    @Autowired
    public UserBadgeController(BadgeProgressService badgeProgressService, BadgeService badgeService) {
        this.badgeProgressService = badgeProgressService;
        this.badgeService = badgeService;
    }

    @GetMapping("/me/badges")
    public ResponseEntity<List<BadgeDTO>> getCurrentUserBadges() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the current user's ID from the authentication context
        org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication
                .getPrincipal();
        Integer userId = ((com.artventuria.api.security.UserPrincipal) userDetails).getId();

        List<Badge> badges = badgeProgressService.getCompletedBadges(userId);
        List<BadgeDTO> badgeDTOs = badges.stream()
                .map(badge -> new BadgeDTO(
                        badge.getId(),
                        badge.getName(),
                        badge.getDescription(),
                        badge.getImageUrl(),
                        badge.getCriteria(),
                        badge.getPoints(),
                        badge.getCreatedAt(),
                        badge.getUpdatedAt()))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(badgeDTOs);
    }

    @GetMapping("/me/badges/progress")
    public ResponseEntity<List<BadgeProgressDTO>> getCurrentUserBadgeProgress() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the current user's ID from the authentication context
        org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication
                .getPrincipal();
        Integer userId = ((com.artventuria.api.security.UserPrincipal) userDetails).getId();

        List<BadgeProgress> progressList = badgeProgressService.getUserBadgeProgresses(userId);
        List<BadgeProgressDTO> progressDTOs = progressList.stream()
                .map(progress -> {
                    Badge badge = progress.getBadge();
                    return new BadgeProgressDTO(
                            progress.getId(),
                            progress.getUserId(),
                            progress.getBadgeId(),
                            progress.getProgress(),
                            progress.getCompleted(),
                            progress.getCreatedAt(),
                            progress.getUpdatedAt(),
                            badge != null ? badge.getName() : null,
                            badge != null ? badge.getDescription() : null,
                            badge != null ? badge.getImageUrl() : null,
                            badge != null ? badge.getCriteria() : null,
                            badge != null ? badge.getPoints() : null
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(progressDTOs);
    }
    
    /**
     * Get all badges (obtained or not) with their status for the current user
     * 
     * @return ResponseEntity with the list of all badges, each with a status field indicating if it is obtained
     */
    @GetMapping("/me/badges/all-with-status")
    public ResponseEntity<List<BadgeWithStatusDTO>> getAllBadgesWithStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the current user's ID from the authentication context
        org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication
                .getPrincipal();
        Integer userId = ((com.artventuria.api.security.UserPrincipal) userDetails).getId();

        // Get all badges from the system
        List<Badge> allBadges = badgeService.getAllBadges();

        // Get all completed badges for the user
        List<Badge> completedBadges = badgeProgressService.getCompletedBadges(userId);
        
        // Create a map of badge ID to completed status
        Map<Integer, Boolean> completedBadgeMap = completedBadges.stream()
                .collect(Collectors.toMap(Badge::getId, badge -> true));
                
        // Get progress for all badges
        List<BadgeProgress> progressList = badgeProgressService.getUserBadgeProgresses(userId);
        
        // Create a map of badge ID to progress
        Map<Integer, Integer> progressMap = progressList.stream()
                .collect(Collectors.toMap(BadgeProgress::getBadgeId, BadgeProgress::getProgress, (a, b) -> a));

        // Convert to DTOs and add the obtained status
        List<BadgeWithStatusDTO> badgeWithStatusDTOs = allBadges.stream()
                .map(badge -> {
                    Boolean obtained = completedBadgeMap.getOrDefault(badge.getId(), false);
                    Integer progress = progressMap.getOrDefault(badge.getId(), 0);
                    return new BadgeWithStatusDTO(
                            badge.getId(),
                            badge.getName(),
                            badge.getDescription(),
                            badge.getImageUrl(),
                            badge.getCriteria(),
                            badge.getPoints(),
                            badge.getCreatedAt(),
                            badge.getUpdatedAt(),
                            obtained,
                            progress
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(badgeWithStatusDTOs);
    }
}