package com.artventuria.api.dto.notification;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationTemplateRequest {
    @NotBlank
    private String name;
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String message;
    
    @NotBlank
    private String type;
    
    private boolean active;
}