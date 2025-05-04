package com.artventuria.api.controller;

import com.artventuria.api.domain.postgresql.Notification;
import com.artventuria.api.service.notification.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public NotificationController(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    private Map<String, Object> generateDataByType(String type, Map<String, Object> requestBody) {
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        data.put("source", "system");

        switch (type) {
            case "new_artwork":
                // If artworkId is provided in the body, we use it
                if (requestBody.containsKey("artworkId")) {
                    data.put("artworkId", requestBody.get("artworkId"));
                    data.put("deepLink", "/artwork/" + requestBody.get("artworkId"));
                }
                break;
            case "badge_earned":
                if (requestBody.containsKey("badgeId")) {
                    data.put("badgeId", requestBody.get("badgeId"));
                    data.put("deepLink", "/profile/badges");
                }
                break;
            case "points_earned":
                if (requestBody.containsKey("points")) {
                    data.put("points", requestBody.get("points"));
                    data.put("deepLink", "/profile/points");
                }
                break;
            case "new_exhibition":
                if (requestBody.containsKey("exhibitionId")) {
                    data.put("exhibitionId", requestBody.get("exhibitionId"));
                    data.put("deepLink", "/exhibition/" + requestBody.get("exhibitionId"));
                }
                break;
        }
        return data;
    }

    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody Map<String, Object> requestBody) {
        try {
            Notification notification = new Notification();
            notification.setUserId((Integer) requestBody.get("userId"));
            notification.setTitle((String) requestBody.get("title"));
            notification.setMessage((String) requestBody.get("message"));
            notification.setType((String) requestBody.get("type"));

            if (requestBody.containsKey("priority")) {
                notification.setPriority((Integer) requestBody.get("priority"));
            }

            // Handle the data field
            Map<String, Object> data;
            if (requestBody.containsKey("data") && requestBody.get("data") != null) {
                // If data is provided in the body, we use it
                Object dataObj = requestBody.get("data");
                if (dataObj instanceof Map) {
                    data = new HashMap<>();
                    // Safe cast and copy to ensure type safety
                    Map<?, ?> dataMap = (Map<?, ?>) dataObj;
                    for (Map.Entry<?, ?> entry : dataMap.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            data.put((String) entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    // Handle case where data is not a map
                    data = new HashMap<>();
                    data.put("rawData", dataObj);
                }
            } else {
                // Otherwise, we generate the data according to the type
                data = generateDataByType((String) requestBody.get("type"), requestBody);
            }

            notification.setData(objectMapper.writeValueAsString(data));
            notificationService.sendNotification(notification);
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON data", e);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, limit, offset));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Integer notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    // New routes

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Integer notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable Integer userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId, limit, offset));
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.countUnreadNotifications(userId));
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(
            @PathVariable Integer userId,
            @PathVariable String type,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(userId, type, limit, offset));
    }
}