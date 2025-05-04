package com.artventuria.api.service.point.impl;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.service.leaderboard.LeaderboardService;
import com.artventuria.api.service.point.PointService;
import com.artventuria.api.service.point.PointTransactionService;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.repository.jpa.user.UserRepository;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final PointTransactionService pointTransactionService;
    private final UserRepository userRepository;

    private final LeaderboardService leaderboardService;

    @Override
    @Transactional(readOnly = true)
    public Integer getUserPoints(Integer userId) {
        return pointTransactionService.getUserBalance(userId);
    }

    @Override
    @Transactional
    public void addPoints(Integer userId, Integer points) {
        addPointsWithArtwork(userId, points, null);
    }

    /**
     * Add points to a user by specifying an artwork (or 0 for system badges)
     * 
     * @param userId    ID of the user
     * @param points    Number of points
     * @param artworkId ID of the artwork (or 0 for system badges)
     */
    @Transactional
    public void addPointsWithArtwork(Integer userId, Integer points, Integer artworkId) {
        // Check if the user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String reason = (artworkId != null && artworkId != 0) ? "Earned points via artwork" : "Earned points via badge";
        pointTransactionService.createTransaction(userId, points, reason, artworkId);

        // Update the ranking directly with the current user's points
        updateLeaderboardForUser(userId);
    }

    @Override
    @Transactional
    public void subtractPoints(Integer userId, Integer points) {
        // Check if the user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        pointTransactionService.createTransaction(userId, -points, "Points déduits", null);

        // Update the ranking
        updateLeaderboardForUser(userId);
    }

    @Override
    @Transactional
    public void awardPoints(Integer userId, Integer artworkId, String reason) {
        // Check if the user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        pointTransactionService.createTransaction(userId, 10, reason, artworkId);

        // No need to update user.points here, it's already done in
        // PointTransactionService
        // Update the ranking directly with the current user's points
        updateLeaderboardForUser(userId);
    }

    /**
     * Update the leaderboard for a user
     * 
     * @param userId User ID
     */
    @Transactional
    private void updateLeaderboardForUser(Integer userId) {
        // Get user total points
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Integer totalPoints = user.getPoints();

        // Update leaderboard using LeaderboardService instead of repository directly
        leaderboardService.updateUserPoints(userId, totalPoints);

        // Update ranks in the leaderboard after updating points
        leaderboardService.updateRanks();
    }
}