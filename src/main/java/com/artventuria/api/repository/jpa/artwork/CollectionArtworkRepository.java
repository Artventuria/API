package com.artventuria.api.repository.jpa.artwork;

import com.artventuria.api.domain.postgresql.CollectionArtwork;
import com.artventuria.api.domain.postgresql.CollectionArtworkId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CollectionArtworkRepository extends JpaRepository<CollectionArtwork, CollectionArtworkId> {
    List<CollectionArtwork> findByCollectionId(Integer collectionId);

    boolean existsByCollectionIdAndArtworkId(Integer collectionId, Integer artworkId);

    @Query("SELECT ca FROM CollectionArtwork ca JOIN ca.collection c WHERE c.userId = :userId AND ca.artworkId = :artworkId")
    List<CollectionArtwork> findByUserIdAndArtworkId(Integer userId, Integer artworkId);
    
    /**
     * Find recently collected artworks across all collections of a user
     * 
     * @param userId User ID
     * @param pageable Pagination and sorting parameters
     * @return List of CollectionArtwork entities ordered by acquisition date
     */
    @Query("SELECT ca FROM CollectionArtwork ca JOIN ca.collection c WHERE c.userId = :userId ORDER BY ca.acquisitionDate DESC")
    List<CollectionArtwork> findRecentlyCollectedByUserId(@Param("userId") Integer userId, Pageable pageable);
    
    /**
     * Find artworks collected within a specific time period across all collections of a user
     * 
     * @param userId User ID
     * @param sinceDate Only include artworks collected after this date
     * @return List of CollectionArtwork entities ordered by acquisition date (most recent first)
     */
    @Query("SELECT ca FROM CollectionArtwork ca JOIN ca.collection c WHERE c.userId = :userId AND ca.acquisitionDate >= :sinceDate ORDER BY ca.acquisitionDate DESC")
    List<CollectionArtwork> findCollectedSinceByUserId(@Param("userId") Integer userId, @Param("sinceDate") Instant sinceDate);
    
    /**
     * Count the number of unique users who have collected a specific artwork
     * 
     * @param artworkId ID of the artwork
     * @return Number of unique users who have collected the artwork
     */
    @Query("SELECT COUNT(DISTINCT c.userId) FROM CollectionArtwork ca JOIN ca.collection c WHERE ca.artworkId = :artworkId")
    int countUsersByArtworkId(@Param("artworkId") Integer artworkId);
    
    /**
     * Get all artworks collected by a user across all of their collections
     * 
     * @param userId ID of the user
     * @param limit Maximum number of items to return
     * @param offset Number of items to skip for pagination
     * @return List of CollectionArtwork entities with their associated artworks
     */
    @Query("SELECT ca FROM CollectionArtwork ca JOIN ca.collection c WHERE c.userId = :userId ORDER BY ca.acquisitionDate DESC")
    List<CollectionArtwork> findAllCollectedByUserId(@Param("userId") Integer userId, Pageable pageable);
    
    /**
     * Count the total number of unique artworks collected by a user across all collections
     * 
     * @param userId ID of the user
     * @return Total count of unique artworks collected by the user
     */
    @Query("SELECT COUNT(DISTINCT ca.artworkId) FROM CollectionArtwork ca JOIN ca.collection c WHERE c.userId = :userId")
    int countUniqueArtworksByUserId(@Param("userId") Integer userId);
}
