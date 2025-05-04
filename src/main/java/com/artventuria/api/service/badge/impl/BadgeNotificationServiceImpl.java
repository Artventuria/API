package com.artventuria.api.service.badge.impl;

import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.domain.postgresql.BadgeProgress;
import com.artventuria.api.domain.postgresql.Notification;
import com.artventuria.api.service.badge.BadgeNotificationService;
import com.artventuria.api.service.notification.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Implementation of the BadgeNotificationService interface
 * Responsible for sending notifications when users earn badges or make progress
 * towards badges
 */
@Service
public class BadgeNotificationServiceImpl implements BadgeNotificationService {

    private final NotificationService notificationService;

    @Autowired
    public BadgeNotificationServiceImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void sendBadgeEarnedNotification(Integer userId, Badge badge, BadgeProgress badgeProgress) {
        // Send notification only, without modifying data
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle("Badge Earned: " + badge.getName());
        notification.setMessage("Congratulations! You've earned the " + badge.getName() + " badge. " +
                (badge.getDescription() != null ? badge.getDescription() : ""));
        notification.setType("badge_earned");
        notification.setPriority(2); // Higher priority for badge earned notifications
        notification.setRead(false);
        notification.setSent(false);

        // Create JSON data with badge details
        String jsonData = String.format("{\"badge_id\":%d,\"badge_name\":\"%s\",\"badge_image\":\"%s\",\"points\":%d}",
                badge.getId(),
                badge.getName(),
                badge.getImageUrl() != null ? badge.getImageUrl() : "",
                badge.getPoints());
        notification.setData(jsonData);

        notification.setCreatedAt(Instant.now());
        notification.setUpdatedAt(Instant.now());

        notificationService.sendNotification(notification);
    }

    @Override
    public void sendBadgeProgressNotification(Integer userId, Badge badge, BadgeProgress badgeProgress, int threshold) {
        // Only send progress notifications at specific thresholds
        if (!shouldSendProgressNotification(badgeProgress, threshold)) {
            return;
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle("Badge Progress: " + badge.getName());
        notification
                .setMessage(String.format("You're making progress! You've reached %d%% towards earning the %s badge.",
                        threshold, badge.getName()));
        notification.setType("badge_progress");
        notification.setPriority(1); // Normal priority for progress notifications
        notification.setRead(false);
        notification.setSent(false);

        // Create JSON data with badge details and progress
        String jsonData = String.format("{\"badge_id\":%d,\"badge_name\":\"%s\",\"progress\":%d,\"threshold\":%d}",
                badge.getId(),
                badge.getName(),
                badgeProgress.getProgress(),
                threshold);
        notification.setData(jsonData);

        notification.setCreatedAt(Instant.now());
        notification.setUpdatedAt(Instant.now());

        notificationService.sendNotification(notification);
    }

    /**
     * Determines if a progress notification should be sent based on the threshold
     * 
     * @param badgeProgress The badge progress record
     * @param threshold     The threshold percentage (e.g., 50, 75)
     * @return true if a notification should be sent, false otherwise
     */
    private boolean shouldSendProgressNotification(BadgeProgress badgeProgress, int threshold) {
        // Don't send progress notifications for completed badges
        if (badgeProgress.getCompleted()) {
            return false;
        }

        Badge badge = badgeProgress.getBadge();
        if (badge == null || badge.getCriteria() == null) {
            return false;
        }

        try {
            // Parse the criteria JSON to get the target value
            com.fasterxml.jackson.databind.JsonNode criteriaNode = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readTree(badge.getCriteria());
            if (!criteriaNode.has("requirements") || !criteriaNode.get("requirements").has("count")) {
                return false;
            }

            int targetCount = criteriaNode.get("requirements").get("count").asInt();
            if (targetCount <= 0) {
                return false;
            }

            // Calculate the actual progress percentage
            int currentProgress = badgeProgress.getProgress();
            int actualPercentage = (currentProgress * 100) / targetCount;

            // Send notifications at 25%, 50%, and 75% thresholds
            return (threshold == 25 && actualPercentage >= 25 && actualPercentage < 50) ||
                    (threshold == 50 && actualPercentage >= 50 && actualPercentage < 75) ||
                    (threshold == 75 && actualPercentage >= 75 && actualPercentage < 100);

        } catch (Exception e) {
            return false;
        }
    }
}