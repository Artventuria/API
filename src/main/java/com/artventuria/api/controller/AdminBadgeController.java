package com.artventuria.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.dto.badge.*;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.service.badge.BadgeService;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
@RequestMapping("/api/admin/badges")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBadgeController {

    private final BadgeService badgeService;

    public AdminBadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @PostMapping
    public ResponseEntity<?> createBadge(@Valid @RequestBody Object request) {
        try {
            // Convert the object based on its type (single object or array)
            ObjectMapper objectMapper = new ObjectMapper();

            if (request instanceof List || objectMapper.writeValueAsString(request).startsWith("[")) {
                // It's an array of badges
                List<CreateBadgeRequest> requests = objectMapper.convertValue(request,
                        new TypeReference<List<CreateBadgeRequest>>() {
                        });

                List<Badge> badges = new ArrayList<>();
                for (CreateBadgeRequest badgeRequest : requests) {
                    badges.add(badgeService.createBadge(badgeRequest));
                }

                List<BadgeResponse> responses = badges.stream()
                        .map(BadgeResponse::fromBadge)
                        .collect(Collectors.toList());

                return ResponseEntity.ok(responses);
            } else {
                // It's a single badge
                CreateBadgeRequest badgeRequest = objectMapper.convertValue(request, CreateBadgeRequest.class);
                Badge badge = badgeService.createBadge(badgeRequest);
                return ResponseEntity.ok(BadgeResponse.fromBadge(badge));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error",
                            "An error occurred while creating the badge(s): " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<BadgeResponse>> getAllBadges() {
        List<BadgeResponse> responses = badgeService.getAllBadges().stream()
                .map(BadgeResponse::fromBadge)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{badgeId}")
    public ResponseEntity<?> getBadgeById(@PathVariable Integer badgeId) {
        try {
            Badge badge = badgeService.getBadgeById(badgeId);
            return ResponseEntity.ok(BadgeResponse.fromBadge(badge));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{badgeId}")
    public ResponseEntity<?> updateBadge(
            @PathVariable Integer badgeId,
            @Valid @RequestBody UpdateBadgeRequest request) {
        try {
            Badge badge = badgeService.updateBadge(badgeId, request);
            return ResponseEntity.ok(BadgeResponse.fromBadge(badge));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error",
                            "An error occurred while updating the badge: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{badgeId}")
    public ResponseEntity<?> deleteBadge(@PathVariable Integer badgeId) {
        try {
            badgeService.deleteBadge(badgeId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error",
                            "An error occurred while deleting the badge: " + e.getMessage()));
        }
    }

    @PatchMapping("/{badgeId}")
    public ResponseEntity<?> patchBadge(
            @PathVariable Integer badgeId,
            @Valid @RequestBody PatchBadgeRequest request) {
        try {
            Badge badge = badgeService.patchBadge(badgeId, request);
            return ResponseEntity.ok(BadgeResponse.fromBadge(badge));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error",
                            "An error occurred while updating the badge: " + e.getMessage()));
        }
    }
}