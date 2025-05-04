package com.artventuria.api.service.consumer;

import com.artventuria.api.config.KafkaConfig;
import com.artventuria.api.dto.notification.NotificationMessage;
import com.artventuria.api.repository.jpa.notification.NotificationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;

@Service
public class NotificationConsumer {
    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);
    private static final String TEST_TOKEN = "test-token";

    private final FirebaseMessaging firebaseMessaging;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationConsumer(FirebaseMessaging firebaseMessaging, NotificationRepository notificationRepository) {
        this.firebaseMessaging = firebaseMessaging;
        this.notificationRepository = notificationRepository;
    }

    private void updateNotificationStatus(Integer notificationId, boolean sent) {
        if (notificationId != null) {
            notificationRepository.findById(notificationId).ifPresent(notification -> {
                notification.setSent(sent);
                notificationRepository.save(notification);
            });
        }
    }

    @KafkaListener(topics = KafkaConfig.PUSH_NOTIFICATION_TOPIC, groupId = "push-notification-group")
    public void consumePushNotification(NotificationMessage message) {
        try {
            if (message.getDeviceToken() != null && !message.getDeviceToken().isEmpty()) {
                // Prepare the data for Firebase
                Map<String, String> firebaseData = new HashMap<>();
                firebaseData.put("type", message.getType());
                if (message.getData() != null) {
                    firebaseData.put("data", message.getData());
                } else {
                    firebaseData.put("data",
                            "{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"source\":\"system\"}");
                }

                // If it's a test token, simulate the sending without calling Firebase
                if (TEST_TOKEN.equals(message.getDeviceToken())) {
                    logger.info("SIMULATION: Notification envoyée avec succès au token de test");
                    logger.info("Titre: {}", message.getTitle());
                    logger.info("Message: {}", message.getMessage());
                    logger.info("Type: {}", message.getType());
                    logger.info("Data: {}", firebaseData);
                    // Mark as sent for the test token
                    updateNotificationStatus(message.getNotificationId(), true);
                    return;
                }

                try {
                    // Otherwise, proceed normally with Firebase
                    Message firebaseMessage = Message.builder()
                            .setToken(message.getDeviceToken())
                            .setNotification(Notification.builder()
                                    .setTitle(message.getTitle())
                                    .setBody(message.getMessage())
                                    .build())
                            .putAllData(firebaseData)
                            .build();

                    String response = firebaseMessaging.send(firebaseMessage);
                    logger.info("Successfully sent push notification: {}", response);
                    // Mark as sent only if Firebase confirms the sending
                    updateNotificationStatus(message.getNotificationId(), true);
                } catch (Exception e) {
                    logger.error("Failed to send push notification: {}", e.getMessage(), e);
                    // Mark as not sent in case of error
                    updateNotificationStatus(message.getNotificationId(), false);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process push notification: {}", e.getMessage(), e);
            // Mark as not sent in case of error
            updateNotificationStatus(message.getNotificationId(), false);
        }
    }

    @KafkaListener(topics = KafkaConfig.NOTIFICATION_TOPIC, groupId = "notification-group")
    public void consumeNotification(NotificationMessage message) {
        logger.info("Received notification: {}", message);
        // Implement any additional processing logic here
    }
}