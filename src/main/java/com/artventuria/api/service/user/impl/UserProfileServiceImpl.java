package com.artventuria.api.service.user.impl;

import com.artventuria.api.domain.postgresql.UserProfile;
import com.artventuria.api.repository.jpa.user.UserProfileRepository;
import com.artventuria.api.service.user.UserProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfile getUserProfile(Integer userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for user id: " + userId));
    }

    @Override
    public UserProfile updateUserProfile(Integer userId, UserProfile profile) {
        UserProfile existingProfile = getUserProfile(userId);
        existingProfile.setBio(profile.getBio());
        existingProfile.setLocation(profile.getLocation());

        return userProfileRepository.save(existingProfile);
    }

    @Override
    public void deleteUserProfile(Integer userId) {
        userProfileRepository.deleteByUserId(userId);
    }

    @Override
    public boolean hasUserProfile(Integer userId) {
        return userProfileRepository.existsByUserId(userId);
    }

    @Override
    public void updateBio(Integer userId, String bio) {
        UserProfile profile = getUserProfile(userId);
        profile.setBio(bio);
        userProfileRepository.save(profile);
    }

    @Override
    public void updateLocation(Integer userId, String location) {
        UserProfile profile = getUserProfile(userId);
        profile.setLocation(location);
        userProfileRepository.save(profile);
    }

    @Override
    public void updateBadgeCount(Integer userId, Integer badgeCount) {
        UserProfile profile = getUserProfile(userId);
        profile.setBadgeCount(badgeCount);
        userProfileRepository.saveAndFlush(profile); // Force the immediate write to the database
        System.out.println("Badge count updated for user ID " + userId + ": " + badgeCount);
    }
}