package com.artventuria.api.service.admin;

import com.artventuria.api.dto.notification.*;
import java.util.List;

public interface AdminNotificationService {
    List<NotificationAnalytics> getNotificationAnalytics(String startDate, String endDate);
    
    List<NotificationTemplate> getNotificationTemplates();
    
    NotificationTemplate createNotificationTemplate(CreateNotificationTemplateRequest request);
    
    NotificationTemplate updateNotificationTemplate(Long id, UpdateNotificationTemplateRequest request);
    
    void deleteNotificationTemplate(Long id);
    
    NotificationSettings getNotificationSettings();
    
    NotificationSettings updateNotificationSettings(UpdateNotificationSettingsRequest request);
}