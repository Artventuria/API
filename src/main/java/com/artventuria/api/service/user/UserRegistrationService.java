package com.artventuria.api.service.user;

import com.artventuria.api.domain.postgresql.*;
import com.artventuria.api.repository.jpa.artwork.CollectionRepository;
import com.artventuria.api.repository.jpa.notification.NotificationPreferenceRepository;
import com.artventuria.api.repository.jpa.points.LeaderboardRepository;
import com.artventuria.api.repository.jpa.user.UserProfileRepository;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.service.auth.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CollectionRepository collectionRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final RoleService roleService;

    @Autowired
    public UserRegistrationService(
            UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            CollectionRepository collectionRepository,
            LeaderboardRepository leaderboardRepository,
            NotificationPreferenceRepository notificationPreferenceRepository,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.collectionRepository = collectionRepository;
        this.leaderboardRepository = leaderboardRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.roleService = roleService;
    }

    @Transactional
    public User registerUser(User user) {
        // Initialize default values
        user.setPoints(0);
        user.setLastLogin(Instant.now());

        // Save the user
        User savedUser = userRepository.save(user);

        // Assign default ROLE_USER role
        roleService.assignRoleToUser(savedUser, "ROLE_USER");

        // Create default profile
        UserProfile profile = new UserProfile();
        profile.setUserId(savedUser.getId());
        profile.setDisplayName(savedUser.getUsername());
        profile.setBadgeCount(0);
        profile.setCollectionCount(1);
        userProfileRepository.save(profile);

        // Create default collection
        Collection collection = new Collection();
        collection.setUserId(savedUser.getId());
        collection.setName("My Collection");
        collection.setDescription("Personal collection of artworks");
        collection.setIsPublic(false);
        collectionRepository.save(collection);

        // Create leaderboard entry
        LeaderboardEntry leaderboard = new LeaderboardEntry();
        leaderboard.setUserId(savedUser.getId());
        leaderboard.setPoints(0);
        leaderboard.setRank((int) (leaderboardRepository.count() + 1));
        leaderboardRepository.save(leaderboard);

        // Create default notification preferences with all options enabled
        NotificationPreference notificationPreference = new NotificationPreference();
        notificationPreference.setUserId(savedUser.getId());
        notificationPreference.setEmailNotifications(true);
        notificationPreference.setPushNotifications(true);
        notificationPreference.setNewArtworkNotify(true);
        notificationPreference.setBadgeEarnedNotify(true);
        notificationPreference.setPointsEarnedNotify(true);
        notificationPreference.setExhibitionNotify(true);
        notificationPreferenceRepository.save(notificationPreference);

        // Update all ranks
        updateLeaderboardRanks();

        return savedUser;
    }

    private void updateLeaderboardRanks() {
        var entries = leaderboardRepository.findAllByOrderByPointsDesc();
        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            entry.setRank(i + 1);
            leaderboardRepository.save(entry);
        }
    }
}