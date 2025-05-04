package com.artventuria.api.service.leaderboard;

import java.util.List;
import com.artventuria.api.domain.postgresql.LeaderboardEntry;

public interface LeaderboardService {
    List<LeaderboardEntry> getLeaderboard(int limit, int offset);

    LeaderboardEntry getUserRank(Integer userId);

    List<LeaderboardEntry> getNearbyUsers(Integer userId);

    void addPoints(Integer userId, int points);

    /**
     * Update the user's points in the leaderboard
     * 
     * @param userId ID of the user
     * @param points Total points
     */
    void updateUserPoints(Integer userId, Integer points);

    /**
     * Recalculate the ranks for all users in the leaderboard
     */
    void updateRanks();
}