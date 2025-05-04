package com.artventuria.api.service.point;

public interface PointService {
    /**
     * Get the total points of a user
     * 
     * @param userId ID of the user
     * @return Total points
     */
    Integer getUserPoints(Integer userId);

    /**
     * Add points to a user and update the ranking
     * 
     * @param userId ID of the user
     * @param points Number of points to add
     */
    void addPoints(Integer userId, Integer points);

    /**
     * Subtract points from a user and update the ranking
     * 
     * @param userId ID of the user
     * @param points Number of points to subtract
     */
    void subtractPoints(Integer userId, Integer points);

    /**
     * Award points for the interaction with an artwork
     * 
     * @param userId    ID of the user
     * @param artworkId ID of the artwork
     * @param reason    Reason for the awarding of points
     */
    void awardPoints(Integer userId, Integer artworkId, String reason);
}