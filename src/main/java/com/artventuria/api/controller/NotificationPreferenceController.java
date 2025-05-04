package com.artventuria.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.artventuria.api.domain.postgresql.NotificationPreference;
import com.artventuria.api.security.UserPrincipal;
import com.artventuria.api.service.notification.NotificationPreferenceService;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/notifications/preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService notificationPreferenceService;

    @Autowired
    public NotificationPreferenceController(NotificationPreferenceService notificationPreferenceService) {
        this.notificationPreferenceService = notificationPreferenceService;
    }

    @GetMapping("/me")
    public ResponseEntity<NotificationPreference> getUserPreferences() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationPreferenceService.getUserPreferences(userPrincipal.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<NotificationPreference> updateUserPreferences(
            @RequestBody NotificationPreference preferences) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationPreferenceService.updateUserPreferences(userPrincipal.getId(), preferences));
    }

    @PostMapping("/enable-all")
    public ResponseEntity<NotificationPreference> enableAllNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationPreferenceService.enableNotifications(userPrincipal.getId()));
    }

    @PostMapping("/disable-all")
    public ResponseEntity<NotificationPreference> disableAllNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationPreferenceService.disableNotifications(userPrincipal.getId()));
    }
}