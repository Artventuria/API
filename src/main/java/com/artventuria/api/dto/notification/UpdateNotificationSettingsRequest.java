package com.artventuria.api.dto.notification;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationSettingsRequest {
    private boolean emailEnabled;
    private boolean pushEnabled;
    private boolean inAppEnabled;
    private String emailTemplate;
    private String pushTemplate;
    private String inAppTemplate;
    
    @Min(1)
    private int batchSize;
    
    @Min(0)
    private int retryAttempts;
    
    @Min(0)
    private int retryDelaySeconds;
}