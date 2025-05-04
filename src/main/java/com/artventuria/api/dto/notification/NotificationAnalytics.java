package com.artventuria.api.dto.notification;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationAnalytics {
    private Long id;
    private String type;
    private Long userId;
    private String title;
    private String message;
    private boolean read;
    private Instant createdAt;
    private Instant readAt;
    private String deviceToken;
    private boolean delivered;
}