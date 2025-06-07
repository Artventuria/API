package com.artventuria.api.service.leaderboard.impl;

import com.artventuria.api.domain.postgresql.LeaderboardEntry;
import com.artventuria.api.repository.jpa.points.LeaderboardRepository;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.service.badge.BadgeProgressService;
import org.springframework.context.annotation.Lazy;
import com.artventuria.api.service.leaderboard.LeaderboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;
    private final BadgeProgressService badgeProgressService;

    @Autowired
    public LeaderboardServiceImpl(LeaderboardRepository leaderboardRepository, UserRepository userRepository, @Lazy BadgeProgressService badgeProgressService) {
        this.leaderboardRepository = leaderboardRepository;
        this.userRepository = userRepository;
        this.badgeProgressService = badgeProgressService;
    }

    @Override
    public List<LeaderboardEntry> getLeaderboard(int limit, int offset) {
        List<LeaderboardEntry> entries = leaderboardRepository.findAllByOrderByPointsDescRankAsc(limit, offset);
        return populateUsernames(entries);
    }

    @Override
    public LeaderboardEntry getUserRank(Integer userId) {
        LeaderboardEntry entry = leaderboardRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found in leaderboard"));
        return populateUsername(entry);
    }

    @Override
    public List<LeaderboardEntry> getNearbyUsers(Integer userId) {
        LeaderboardEntry userEntry = getUserRank(userId);
        List<LeaderboardEntry> entries = leaderboardRepository.findNearbyUsers(userEntry.getRank(), 5);
        return populateUsernames(entries);
    }

    private LeaderboardEntry populateUsername(LeaderboardEntry entry) {
        if (entry.getUserId() != null) {
            userRepository.findById(entry.getUserId()).ifPresent(user -> {
                entry.setUsername(user.getUsername());
            });
            
            // Calculate badge count - number of completed badges for the user
            int completedBadgesCount = badgeProgressService.getCompletedBadges(entry.getUserId()).size();
            entry.setBadgeCount(completedBadgesCount);
        }
        return entry;
    }

    private List<LeaderboardEntry> populateUsernames(List<LeaderboardEntry> entries) {
        for (LeaderboardEntry entry : entries) {
            populateUsername(entry);
        }
        return entries;
    }

    @Override
    public void addPoints(Integer userId, int points) {
        LeaderboardEntry entry = leaderboardRepository.findByUserId(userId)
                .orElse(new LeaderboardEntry());

        if (entry.getId() == null) {
            entry.setUserId(userId);
            entry.setPoints(points);
            entry.setRank((int) (leaderboardRepository.count() + 1));
        } else {
            entry.setPoints(entry.getPoints() + points);
        }

        entry.setUpdatedAt(Instant.now());
        leaderboardRepository.save(entry);
        updateRanks();
    }

    @Override
    public void updateRanks() {
        // Use the optimized SQL method to update all ranks in a single query
        leaderboardRepository.updateRanks();
    }

    @Override
    public void updateUserPoints(Integer userId, Integer points) {
        // Get the leaderboard entry for the user or create a new one
        LeaderboardEntry entry = leaderboardRepository.findByUserId(userId)
                .orElse(new LeaderboardEntry());

        if (entry.getId() == null) {
            // New entry
            entry.setUserId(userId);
            entry.setPoints(points);
            entry.setRank((int) (leaderboardRepository.count() + 1));
        } else {
            // Update the points
            entry.setPoints(points);
        }

        entry.setUpdatedAt(Instant.now());
        leaderboardRepository.save(entry);

        // Do not call updateRanks() here to allow batch updates
        // without recalculating the ranking each time
    }
}