package com.artventuria.api.service.notification;

import com.artventuria.api.domain.postgresql.NotificationPreference;

/**
 * Service for managing user notification preferences
 */
public interface NotificationPreferenceService {
    /**
     * Get the notification preferences of a user
     * 
     * @param userId ID of the user
     * @return The notification preferences of the user
     */
    NotificationPreference getUserPreferences(Integer userId);

    /**
     * Update the notification preferences of a user
     * 
     * @param userId      ID of the user
     * @param preferences New notification preferences
     * @return The updated notification preferences
     */
    NotificationPreference updateUserPreferences(Integer userId, NotificationPreference preferences);

    /**
     * Enable notifications for a user
     * 
     * @param userId ID of the user
     * @return The updated notification preferences
     */
    NotificationPreference enableNotifications(Integer userId);

    /**
     * Disable notifications for a user
     * 
     * @param userId ID of the user
     * @return The updated notification preferences
     */
    NotificationPreference disableNotifications(Integer userId);
}