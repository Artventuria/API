package com.artventuria.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import com.artventuria.api.dto.notification.*;
import com.artventuria.api.service.admin.AdminNotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    public AdminNotificationController(AdminNotificationService adminNotificationService) {
        this.adminNotificationService = adminNotificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationAnalytics>> getNotificationAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(adminNotificationService.getNotificationAnalytics(startDate, endDate));
    }

    @GetMapping("/templates")
    public ResponseEntity<List<NotificationTemplate>> getNotificationTemplates() {
        return ResponseEntity.ok(adminNotificationService.getNotificationTemplates());
    }

    @PostMapping("/templates")
    public ResponseEntity<NotificationTemplate> createNotificationTemplate(
            @Valid @RequestBody CreateNotificationTemplateRequest request) {
        return ResponseEntity.ok(adminNotificationService.createNotificationTemplate(request));
    }

    @PutMapping("/templates/{id}")
    public ResponseEntity<NotificationTemplate> updateNotificationTemplate(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNotificationTemplateRequest request) {
        return ResponseEntity.ok(adminNotificationService.updateNotificationTemplate(id, request));
    }

    @DeleteMapping("/templates/{id}")
    public ResponseEntity<Void> deleteNotificationTemplate(@PathVariable Long id) {
        adminNotificationService.deleteNotificationTemplate(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/settings")
    public ResponseEntity<NotificationSettings> getNotificationSettings() {
        return ResponseEntity.ok(adminNotificationService.getNotificationSettings());
    }

    @PutMapping("/settings")
    public ResponseEntity<NotificationSettings> updateNotificationSettings(
            @Valid @RequestBody UpdateNotificationSettingsRequest request) {
        return ResponseEntity.ok(adminNotificationService.updateNotificationSettings(request));
    }
}