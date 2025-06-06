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
}
