package com.artventuria.api.dto.notification;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings {
    private Long id;
    private boolean emailEnabled;
    private boolean pushEnabled;
    private boolean inAppEnabled;
    private String emailTemplate;
    private String pushTemplate;
    private String inAppTemplate;
    private int batchSize;
    private int retryAttempts;
    private int retryDelaySeconds;
}