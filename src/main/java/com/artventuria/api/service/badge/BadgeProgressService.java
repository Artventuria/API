package com.artventuria.api.service.badge;

import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.domain.postgresql.BadgeProgress;

import java.util.List;

/**
 * // Service for managing badge progress
 */
public interface BadgeProgressService {

    /**
     * Get the progress of a badge for a user
     * 
     * @param userId  User ID
     * @param badgeId Badge ID
     * @return The progress of the badge
     */
    BadgeProgress getBadgeProgress(Integer userId, Integer badgeId);

    /**
     * Update the progress of a badge for a user
     * 
     * @param userId            User ID
     * @param badgeId           Badge ID
     * @param progressIncrement Progress increment
     * @return The updated progress
     */
    BadgeProgress updateProgress(Integer userId, Integer badgeId, Integer progressIncrement);

    /**
     * Award a badge directly to a user
     * 
     * @param userId  User ID
     * @param badgeId Badge ID
     */
    void awardBadge(Integer userId, Integer badgeId);

    /**
     * Get the list of completed badges for a user
     * 
     * @param userId User ID
     * @return List of completed badges
     */
    List<Badge> getCompletedBadges(Integer userId);

    /**
     * Get the list of badge progresses for a user
     * 
     * @param userId User ID
     * @return List of badge progresses
     */
    List<BadgeProgress> getUserBadgeProgresses(Integer userId);

    /**
     * Calculate the progress percentage for a badge
     * 
     * @param badgeProgress The progress of the badge
     * @param badge         The badge
     * @return The progress percentage (0-100)
     */
    Integer calculateProgressPercentage(BadgeProgress badgeProgress, Badge badge);
}
