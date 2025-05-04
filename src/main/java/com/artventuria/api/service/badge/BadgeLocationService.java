package com.artventuria.api.service.badge;

/**
 * Service for managing "location" type badges
 */
public interface BadgeLocationService {

    /**
     * Counts the number of unique venues visited by a user
     * based on valid scans only
     * 
     * @param userId User ID
     * @return number of unique venues visited
     */
    int countUniqueVenuesForUser(Long userId);
}
