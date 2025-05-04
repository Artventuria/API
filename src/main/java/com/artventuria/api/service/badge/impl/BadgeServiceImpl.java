package com.artventuria.api.service.badge.impl;

import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.dto.badge.CreateBadgeRequest;
import com.artventuria.api.dto.badge.UpdateBadgeRequest;
import com.artventuria.api.service.badge.BadgeService;
import com.artventuria.api.service.user.UserProfileService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.repository.jpa.badge.BadgeRepository;
import com.artventuria.api.dto.badge.PatchBadgeRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BadgeServiceImpl implements BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public Badge createBadge(CreateBadgeRequest request) {
        Badge badge = new Badge();
        badge.setName(request.getName());
        badge.setDescription(request.getDescription());
        badge.setImageUrl(request.getImageUrl());
        badge.setPoints(request.getPoints());

        // Use LinkedHashMap to preserve the order of the fields
        Map<String, Object> criteriaMap = new LinkedHashMap<>();
        criteriaMap.put("type", request.getType());
        criteriaMap.put("requirements", request.getCriteria());

        // Convert the map to JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String criteriaJson = objectMapper.writeValueAsString(criteriaMap);
            badge.setCriteria(criteriaJson);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating badge criteria: " + e.getMessage());
        }

        return badgeRepository.save(badge);
    }

    @Override
    public Badge updateBadge(Integer badgeId, UpdateBadgeRequest request) {
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found"));

        badge.setName(request.getName());
        badge.setDescription(request.getDescription());
        badge.setImageUrl(request.getImageUrl());
        badge.setCriteria(request.getCriteria());
        badge.setPoints(request.getPoints());

        return badgeRepository.save(badge);
    }

    @Override
    public void deleteBadge(Integer badgeId) {
        badgeRepository.deleteById(badgeId);
    }

    @Override
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    @Override
    public Badge getBadgeById(Integer badgeId) {
        return badgeRepository.findById(badgeId)
                .orElseThrow(() -> new com.artventuria.api.exception.ResourceNotFoundException(
                        "Badge not found with id: " + badgeId));
    }

    @Override
    public void updateUserBadgeCount(Integer userId, Integer badgeCount) {
        // Delegate the badge count update to the UserProfileService
        userProfileService.updateBadgeCount(userId, badgeCount);
    }

    @Override
    public Badge patchBadge(Integer badgeId, PatchBadgeRequest request) {
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found with id: " + badgeId));

        // Update only non-null fields
        if (request.getName() != null) {
            badge.setName(request.getName());
        }

        if (request.getDescription() != null) {
            badge.setDescription(request.getDescription());
        }

        if (request.getImageUrl() != null) {
            badge.setImageUrl(request.getImageUrl());
        }

        if (request.getPoints() != null) {
            badge.setPoints(request.getPoints());
        }

        // Update the criteria if type or criteria are provided
        if (request.getType() != null || request.getCriteria() != null) {
            try {
                // Read the existing criteria
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> existingCriteriaMap = objectMapper.readValue(badge.getCriteria(),
                        new TypeReference<LinkedHashMap<String, Object>>() {
                        });

                // Update the type if provided
                if (request.getType() != null) {
                    existingCriteriaMap.put("type", request.getType());
                }

                // Update the requirements if provided
                if (request.getCriteria() != null) {
                    existingCriteriaMap.put("requirements", request.getCriteria());
                }

                // Convert the updated map to JSON
                String updatedCriteriaJson = objectMapper.writeValueAsString(existingCriteriaMap);
                badge.setCriteria(updatedCriteriaJson);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error updating badge criteria: " + e.getMessage());
            }
        }

        return badgeRepository.save(badge);
    }
}