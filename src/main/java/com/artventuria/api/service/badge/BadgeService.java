package com.artventuria.api.service.badge;

import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.dto.badge.CreateBadgeRequest;
import com.artventuria.api.dto.badge.UpdateBadgeRequest;
import com.artventuria.api.dto.badge.PatchBadgeRequest;

import java.util.List;

/**
 * Service for managing badges (CRUD operations)
 */
public interface BadgeService {
    /**
     * Create a new badge
     * 
     * @param request Information about the badge to create
     * @return The created badge
     */
    Badge createBadge(CreateBadgeRequest request);

    /**
     * Get all badges
     * 
     * @return List of all badges
     */
    List<Badge> getAllBadges();

    /**
     * Get a badge by its ID
     * 
     * @param badgeId Badge ID
     * @return The requested badge
     */
    Badge getBadgeById(Integer badgeId);

    /**
     * Update an existing badge
     * 
     * @param badgeId Badge ID
     * @param request Information about the badge to update
     * @return The updated badge
     */
    Badge updateBadge(Integer badgeId, UpdateBadgeRequest request);

    /**
     * Update a partially existing badge
     * 
     * @param badgeId Badge ID
     * @param request Information about the badge to update
     * @return The updated badge
     */
    Badge patchBadge(Integer badgeId, PatchBadgeRequest request);

    /**
     * Delete a badge
     * 
     * @param badgeId Badge ID
     */
    void deleteBadge(Integer badgeId);

    /**
     * Update the badge counter in the user profile
     * 
     * @param userId     User ID
     * @param badgeCount Number of badges
     */
    void updateUserBadgeCount(Integer userId, Integer badgeCount);
}