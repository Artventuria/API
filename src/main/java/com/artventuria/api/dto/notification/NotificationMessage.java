package com.artventuria.api.dto.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private Integer userId;
    private String type;
    private String title;
    private String message;
    private String data;
    private String deviceToken;
    private Integer notificationId;
}