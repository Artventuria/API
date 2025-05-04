package com.artventuria.api.service.notification;

import java.util.List;
import com.artventuria.api.domain.postgresql.Notification;

/**
 * Service for managing notifications
 */
public interface NotificationService {
    /**
     * Send a notification to a user
     * 
     * @param notification The notification to send
     */
    void sendNotification(Notification notification);

    /**
     * Get the notifications of a user
     * 
     * @param userId ID of the user
     * @param limit  Maximum number of notifications to retrieve
     * @param offset Offset for pagination
     * @return List of notifications
     */
    List<Notification> getUserNotifications(Integer userId, int limit, int offset);

    /**
     * Mark a notification as read
     * 
     * @param notificationId ID of the notification
     */
    void markNotificationAsRead(Integer notificationId);

    /**
     * Delete a notification
     * 
     * @param notificationId ID of the notification
     */
    void deleteNotification(Integer notificationId);

    /**
     * Mark all notifications of a user as read
     * 
     * @param userId ID of the user
     */
    void markAllNotificationsAsRead(Integer userId);

    /**
     * Get the unread notifications of a user
     * 
     * @param userId ID of the user
     * @param limit  Maximum number of notifications to retrieve
     * @param offset Offset for pagination
     * @return List of unread notifications
     */
    List<Notification> getUnreadNotifications(Integer userId, int limit, int offset);

    /**
     * Count the number of unread notifications of a user
     * 
     * @param userId ID of the user
     * @return Number of unread notifications
     */
    Long countUnreadNotifications(Integer userId);

    /**
     * Get the notifications of a user by type
     * 
     * @param userId ID of the user
     * @param type   Type of notification
     * @param limit  Maximum number of notifications to retrieve
     * @param offset Offset for pagination
     * @return List of notifications of the specified type
     */
    List<Notification> getNotificationsByType(Integer userId, String type, int limit, int offset);
}