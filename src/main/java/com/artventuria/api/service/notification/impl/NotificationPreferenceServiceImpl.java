package com.artventuria.api.service.notification.impl;

import com.artventuria.api.domain.postgresql.NotificationPreference;
import com.artventuria.api.repository.jpa.notification.NotificationPreferenceRepository;
import com.artventuria.api.service.notification.NotificationPreferenceService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    public NotificationPreferenceServiceImpl(
            NotificationPreferenceRepository notificationPreferenceRepository) {
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    @Override
    public NotificationPreference getUserPreferences(Integer userId) {
        return notificationPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Notification preferences not found"));
    }

    @Override
    @Transactional
    public NotificationPreference updateUserPreferences(Integer userId, NotificationPreference preferences) {
        NotificationPreference existingPreferences = notificationPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Notification preferences not found"));

        // Keep the existing createdAt value
        Instant existingCreatedAt = existingPreferences.getCreatedAt();

        // Update only the provided fields
        if (preferences.getEmailNotifications() != null) {
            existingPreferences.setEmailNotifications(preferences.getEmailNotifications());
        }
        if (preferences.getPushNotifications() != null) {
            existingPreferences.setPushNotifications(preferences.getPushNotifications());
        }
        if (preferences.getNewArtworkNotify() != null) {
            existingPreferences.setNewArtworkNotify(preferences.getNewArtworkNotify());
        }
        if (preferences.getBadgeEarnedNotify() != null) {
            existingPreferences.setBadgeEarnedNotify(preferences.getBadgeEarnedNotify());
        }
        if (preferences.getPointsEarnedNotify() != null) {
            existingPreferences.setPointsEarnedNotify(preferences.getPointsEarnedNotify());
        }
        if (preferences.getExhibitionNotify() != null) {
            existingPreferences.setExhibitionNotify(preferences.getExhibitionNotify());
        }
        if (preferences.getDeviceToken() != null) {
            existingPreferences.setDeviceToken(preferences.getDeviceToken());
        }

        // Ensure createdAt is not modified
        existingPreferences.setCreatedAt(existingCreatedAt);

        return notificationPreferenceRepository.save(existingPreferences);
    }

    @Override
    @Transactional
    public NotificationPreference enableNotifications(Integer userId) {
        NotificationPreference preferences = getUserPreferences(userId);
        preferences.setPushNotifications(true);
        return notificationPreferenceRepository.save(preferences);
    }

    @Override
    @Transactional
    public NotificationPreference disableNotifications(Integer userId) {
        NotificationPreference preferences = getUserPreferences(userId);
        preferences.setPushNotifications(false);
        return notificationPreferenceRepository.save(preferences);
    }
}
