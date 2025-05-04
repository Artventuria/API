package com.artventuria.api.dto.notification;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {
    private Long id;
    private String name;
    private String title;
    private String message;
    private String type;
    private boolean active;
}