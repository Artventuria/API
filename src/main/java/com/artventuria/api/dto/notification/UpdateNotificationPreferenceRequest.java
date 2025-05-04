package com.artventuria.api.dto.notification;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.artventuria.api.domain.postgresql.NotificationPreference;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationPreferenceRequest {
    private boolean emailNotifications;
    private boolean pushNotifications;
    private String deviceToken;

    public NotificationPreference toNotificationPreference() {
        NotificationPreference preference = new NotificationPreference();
        preference.setEmailNotifications(this.emailNotifications);
        preference.setPushNotifications(this.pushNotifications);
        preference.setDeviceToken(this.deviceToken);
        return preference;
    }
}