package com.artventuria.api.repository.mongo;

import com.artventuria.api.domain.mongodb.ScanLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ScanLogRepository extends MongoRepository<ScanLog, String> {
        List<ScanLog> findByUserIdOrderByTimestampDesc(Long userId);

        List<ScanLog> findByArtworkIdOrderByTimestampDesc(Long artworkId);

        List<ScanLog> findByUserIdAndTimestampBetweenOrderByTimestampDesc(
                        Long userId,
                        Instant startTime,
                        Instant endTime);

        List<ScanLog> findByArtworkIdAndTimestampBetweenOrderByTimestampDesc(
                        Long artworkId,
                        Instant startTime,
                        Instant endTime);

        List<ScanLog> findByTimestampBetweenOrderByTimestampDesc(
                        Instant startTime,
                        Instant endTime);

        List<ScanLog> findByTagId(Long tagId);

        @Query(value = "{userId: ?0}", delete = true)
        void deleteByUserId(Long userId);

        @Query(value = "{artworkId: ?0}", delete = true)
        void deleteByArtworkId(Long artworkId);

        /**
         * Find all valid scans for a given user
         * 
         * @param userId ID of the user
         * @return List of valid scans with only the artwork ID
         */
        @Query(value = "{ 'userId': ?0, 'isValid': true }", fields = "{ 'artworkId': 1 }")
        List<ScanLog> findValidArtworkScansByUserId(Long userId);
}