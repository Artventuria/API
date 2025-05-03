package com.artventuria.api.service.user;

import com.artventuria.api.domain.postgresql.UserProfile;

public interface UserProfileService {
    UserProfile getUserProfile(Integer userId);

    UserProfile updateUserProfile(Integer userId, UserProfile profile);

    void deleteUserProfile(Integer userId);

    boolean hasUserProfile(Integer userId);

    void updateBio(Integer userId, String bio);

    void updateLocation(Integer userId, String location);

    /**
     * Updates the badge counter in the user profile
     * 
     * @param userId     ID of the user
     * @param badgeCount Number of badges
     */
    void updateBadgeCount(Integer userId, Integer badgeCount);
}