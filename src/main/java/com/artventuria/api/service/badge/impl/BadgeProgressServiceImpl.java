package com.artventuria.api.service.badge.impl;

import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.domain.postgresql.BadgeProgress;
import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.repository.jpa.badge.BadgeProgressRepository;
import com.artventuria.api.repository.jpa.badge.BadgeRepository;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.service.badge.BadgeLocationService;
import com.artventuria.api.service.badge.BadgeNotificationService;
import com.artventuria.api.service.badge.BadgeProgressService;
import com.artventuria.api.service.point.PointService;
import com.artventuria.api.service.user.UserProfileService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeProgressServiceImpl implements BadgeProgressService {

    private final BadgeProgressRepository badgeProgressRepository;
    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final PointService pointService;
    private final BadgeLocationService badgeLocationService;
    private final EntityManager entityManager;
    private final UserProfileService userProfileService;
    private final BadgeNotificationService badgeNotificationService;

    @Override
    @Transactional(readOnly = true)
    public BadgeProgress getBadgeProgress(Integer userId, Integer badgeId) {
        // Check if a progress already exists
        Optional<BadgeProgress> existingProgress = badgeProgressRepository.findByUserIdAndBadgeId(userId, badgeId);

        if (existingProgress.isPresent()) {
            return existingProgress.get();
        }

        // If not, create a new progress with an initial value of 0
        // Check if the user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if the badge exists
        badgeRepository.findById(badgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found"));

        BadgeProgress newProgress = new BadgeProgress();
        newProgress.setUserId(userId);
        newProgress.setBadgeId(badgeId);
        newProgress.setProgress(0);
        newProgress.setCompleted(false);

        return badgeProgressRepository.save(newProgress);
    }

    @Override
    @Transactional
    public BadgeProgress updateProgress(Integer userId, Integer badgeId, Integer progressIncrement) {
        // Get the current progress or create a new one
        BadgeProgress progress = getBadgeProgress(userId, badgeId);
        int oldProgress = progress.getProgress();
        int newProgress = oldProgress + progressIncrement;
        progress.setProgress(newProgress);

        // Get the associated badge
        Badge badge = badgeRepository.findById(badgeId).orElse(null);
        boolean wasCompleted = progress.getCompleted();

        // Save the updated progress
        BadgeProgress savedProgress = badgeProgressRepository.save(progress);

        // Check if the badge is completed according to its criteria
        if (badge != null && !wasCompleted) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> criteriaMap = objectMapper.readValue(badge.getCriteria(),
                        new TypeReference<Map<String, Object>>() {
                        });
                String badgeType = (String) criteriaMap.get("type");
                @SuppressWarnings("unchecked")
                Map<String, Object> requirements = (Map<String, Object>) criteriaMap.get("requirements");

                if (requirements != null) {
                    int requiredProgress = 1; // Default value

                    // Determine the required progress based on the badge type
                    if ("collection".equals(badgeType) && requirements.containsKey("artwork_count")
                            && requirements.get("artwork_count") != null) {
                        requiredProgress = ((Number) requirements.get("artwork_count")).intValue();
                    } else if ("streak".equals(badgeType) && requirements.containsKey("consecutive_days")
                            && requirements.get("consecutive_days") != null) {
                        requiredProgress = ((Number) requirements.get("consecutive_days")).intValue();
                    } else if ("location".equals(badgeType) && requirements.containsKey("location_count")
                            && requirements.get("location_count") != null) {
                        requiredProgress = ((Number) requirements.get("location_count")).intValue();

                        // For location badges, recalculate the number of unique venues visited
                        int uniqueLocationsCount = badgeLocationService.countUniqueVenuesForUser(userId.longValue());
                        newProgress = uniqueLocationsCount;
                        progress.setProgress(newProgress); // Update with the actual number
                        savedProgress = badgeProgressRepository.save(progress);
                    }

                    // Check if the badge is completed
                    if (newProgress >= requiredProgress && !savedProgress.getCompleted()) {
                        // Mark the badge as completed
                        savedProgress.setCompleted(true);
                        badgeProgressRepository.save(savedProgress);

                        // Add the badge to the user and the associated points
                        awardBadgeToUser(userId, badgeId);
                    }

                    // Calculate the progress percentage for notifications
                    if (requiredProgress > 0) {
                        calculateProgressPercentage(savedProgress, badge);
                    }
                }
            } catch (Exception e) {
                // Log the error without interrupting the flow
                System.err.println("Error updating badge progress: " + e.getMessage());
            }
        }

        return savedProgress;
    }

    @Override
    @Transactional
    public void awardBadge(Integer userId, Integer badgeId) {
        // Get or create the progress
        BadgeProgress progress = getBadgeProgress(userId, badgeId);

        // Mark as completed
        progress.setCompleted(true);
        progress.setProgress(1);

        badgeProgressRepository.save(progress);
        entityManager.flush(); // Force the write to the database

        // Award the badge and the associated points
        awardBadgeToUser(userId, badgeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Badge> getCompletedBadges(Integer userId) {
        return badgeProgressRepository.findCompletedBadgesByUserId(userId).stream()
                .map(progress -> badgeRepository.findById(progress.getBadgeId()).orElse(null))
                .filter(badge -> badge != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeProgress> getUserBadgeProgresses(Integer userId) {
        return badgeProgressRepository.findByUserId(userId);
    }

    @Override
    public Integer calculateProgressPercentage(BadgeProgress badgeProgress, Badge badge) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> criteriaMap = objectMapper.readValue(badge.getCriteria(),
                    new TypeReference<Map<String, Object>>() {
                    });

            @SuppressWarnings("unchecked")
            Map<String, Object> requirements = (Map<String, Object>) criteriaMap.get("requirements");
            String badgeType = (String) criteriaMap.get("type");

            if (requirements != null) {
                int requiredProgress = 1; // Default value

                // Determine the required progress based on the badge type
                if ("collection".equals(badgeType) && requirements.containsKey("artwork_count")
                        && requirements.get("artwork_count") != null) {
                    requiredProgress = ((Number) requirements.get("artwork_count")).intValue();
                } else if ("streak".equals(badgeType) && requirements.containsKey("consecutive_days")
                        && requirements.get("consecutive_days") != null) {
                    requiredProgress = ((Number) requirements.get("consecutive_days")).intValue();
                } else if ("location".equals(badgeType) && requirements.containsKey("location_count")
                        && requirements.get("location_count") != null) {
                    requiredProgress = ((Number) requirements.get("location_count")).intValue();
                }

                // Avoid division by zero
                if (requiredProgress > 0) {
                    int progressValue = badgeProgress.getProgress();
                    // Limit the percentage to 100%
                    int percentage = Math.min((progressValue * 100) / requiredProgress, 100);
                    return percentage;
                }
            }

            // Default value if impossible to calculate
            return badgeProgress.getCompleted() ? 100 : 0;

        } catch (Exception e) {
            System.err.println("Error calculating badge progress percentage: " + e.getMessage());
            return 0; // Default value in case of error
        }
    }

    /**
     * Private method to award a badge and the associated points to a user
     * 
     * @param userId  User ID
     * @param badgeId Badge ID
     */
    @Transactional
    private void awardBadgeToUser(Integer userId, Integer badgeId) {
        User user = userRepository.findById(userId).orElse(null);
        Badge badge = badgeRepository.findById(badgeId).orElse(null);

        if (user != null && badge != null) {
            // Add the badge to the user if they don't already have it
            if (user.getBadges().stream().noneMatch(b -> b.getId().equals(badge.getId()))) {
                user.getBadges().add(badge);
                userRepository.save(user);
                entityManager.flush(); // Force the write to the database

                // Award the associated points to the badge
                if (badge.getPoints() != null && badge.getPoints() > 0) {
                    pointService.addPoints(userId, badge.getPoints());
                }

                // Update the badge count of the user
                Integer badgeCount = badgeRepository.countBadgesByUserId(userId);
                userProfileService.updateBadgeCount(userId, badgeCount);
                System.out.println("Badge count updated for user ID " + userId + ": " + badgeCount);

                try {
                    // Get the BadgeProgress to send the notification
                    BadgeProgress badgeProgress = badgeProgressRepository.findByUserIdAndBadgeId(userId, badgeId)
                            .orElseThrow(() -> new ResourceNotFoundException("Badge progress not found"));

                    // Send a notification for the badge earned
                    badgeNotificationService.sendBadgeEarnedNotification(userId, badge, badgeProgress);
                    System.out.println(
                            "Badge earned notification sent for badge: " + badge.getName() + " to user: " + userId);
                } catch (Exception e) {
                    // Log the error but continue the flow
                    System.err.println("Error sending badge notification: " + e.getMessage());
                }
            }
        }
    }
}
