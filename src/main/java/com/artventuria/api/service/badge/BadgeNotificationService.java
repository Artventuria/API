package com.artventuria.api.service.badge;

import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.domain.postgresql.BadgeProgress;

/**
 * Service responsible for handling badge-related notifications
 * This service is used to send notifications when a user earns a badge
 */
public interface BadgeNotificationService {
    
    /**
     * Send a notification to a user when they earn a badge
     * 
     * @param userId The ID of the user who earned the badge
     * @param badge The badge that was earned
     * @param badgeProgress The badge progress record
     */
    void sendBadgeEarnedNotification(Integer userId, Badge badge, BadgeProgress badgeProgress);
    
    /**
     * Send a notification to a user about their progress towards a badge
     * 
     * @param userId The ID of the user
     * @param badge The badge being progressed towards
     * @param badgeProgress The badge progress record
     * @param threshold The threshold at which to send progress notifications (e.g., 50%, 75%)
     */
    void sendBadgeProgressNotification(Integer userId, Badge badge, BadgeProgress badgeProgress, int threshold);
}