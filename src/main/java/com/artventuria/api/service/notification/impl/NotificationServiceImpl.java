package com.artventuria.api.service.notification.impl;

import com.artventuria.api.domain.postgresql.Notification;
import com.artventuria.api.domain.postgresql.NotificationPreference;
import com.artventuria.api.service.notification.NotificationPreferenceService;
import com.artventuria.api.service.notification.NotificationService;
import com.artventuria.api.dto.notification.NotificationMessage;
import com.artventuria.api.repository.jpa.notification.NotificationRepository;
import com.artventuria.api.config.KafkaConfig;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceService notificationPreferenceService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationPreferenceService notificationPreferenceService,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.notificationRepository = notificationRepository;
        this.notificationPreferenceService = notificationPreferenceService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public void sendNotification(Notification notification) {
        // First, ensure the notification is saved in the database
        // independently of any problem with preferences or Kafka
        notification = notificationRepository.save(notification);

        try {
            // Try to get the notification preferences of the user
            NotificationPreference preferences = null;

            try {
                preferences = notificationPreferenceService.getUserPreferences(notification.getUserId());
            } catch (EntityNotFoundException e) {
                // If the preferences do not exist, log the information but continue the
                // processing
                System.out.println("Notification preferences not found for user ID: " + notification.getUserId()
                        + ", notification saved without sending to Kafka");
                return; // The notification is already saved, we can return without error
            }

            // Build the message for Kafka
            NotificationMessage message = NotificationMessage.builder()
                    .userId(notification.getUserId())
                    .type(notification.getType())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .data(notification.getData())
                    .notificationId(notification.getId())
                    .deviceToken(preferences != null ? preferences.getDeviceToken() : null)
                    .build();

            // Send to the main notification topic
            kafkaTemplate.send(KafkaConfig.NOTIFICATION_TOPIC, message);

            // Send to the push notification topic if enabled
            if (preferences != null && preferences.getPushNotifications() && preferences.getDeviceToken() != null) {
                kafkaTemplate.send(KafkaConfig.PUSH_NOTIFICATION_TOPIC, message);
            }
        } catch (Exception e) {
            // We log the error but we do not throw an exception
            // to avoid interrupting the execution flow, because the notification is
            // already saved
            System.err.println("Error sending notification to Kafka: " + e.getMessage());
        }
    }

    @Override
    public List<Notification> getUserNotifications(Integer userId, int limit, int offset) {
        return notificationRepository.findByUserId(userId, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalse(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public List<Notification> getUnreadNotifications(Integer userId, int limit, int offset) {
        return notificationRepository.findByUserIdAndReadFalse(userId, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public Long countUnreadNotifications(Integer userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public List<Notification> getNotificationsByType(Integer userId, String type, int limit, int offset) {
        return notificationRepository.findByUserIdAndType(userId, type, PageRequest.of(offset, limit)).getContent();
    }
}