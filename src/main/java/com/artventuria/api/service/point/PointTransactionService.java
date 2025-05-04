package com.artventuria.api.service.point;

import com.artventuria.api.domain.postgresql.PointTransaction;

import java.util.List;

/**
 * Service for managing point transactions
 */
public interface PointTransactionService {

    /**
     * Create a new point transaction
     * 
     * @param userId    ID of the user
     * @param points    Number of points (positive to add, negative to subtract)
     * @param reason    Reason for the transaction
     * @param artworkId ID of the artwork associated (can be null)
     * @return The created transaction
     */
    PointTransaction createTransaction(Integer userId, Integer points, String reason, Integer artworkId);

    /**
     * Get the history of a user's transactions
     * 
     * @param userId ID of the user
     * @return List of transactions
     */
    List<PointTransaction> getUserTransactions(Integer userId);

    /**
     * Get the total balance of a user's points
     * 
     * @param userId ID of the user
     * @return Balance of points
     */
    Integer getUserBalance(Integer userId);
}
