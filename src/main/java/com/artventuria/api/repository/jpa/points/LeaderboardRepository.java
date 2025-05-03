package com.artventuria.api.repository.jpa.points;

import com.artventuria.api.domain.postgresql.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry, Integer> {
       Optional<LeaderboardEntry> findByUserId(Integer userId);

       @Query(value = "SELECT * FROM leaderboards ORDER BY points DESC, rank ASC LIMIT :limit OFFSET :offset", nativeQuery = true)
       List<LeaderboardEntry> findAllByOrderByPointsDescRankAsc(@Param("limit") int limit, @Param("offset") int offset);

       @Query(value = "SELECT * FROM leaderboards ORDER BY points DESC", nativeQuery = true)
       List<LeaderboardEntry> findAllByOrderByPointsDesc();

       @Query(value = "SELECT * FROM leaderboards WHERE rank BETWEEN (:rank - :range) AND (:rank + :range) ORDER BY rank ASC", nativeQuery = true)
       List<LeaderboardEntry> findNearbyUsers(@Param("rank") int rank, @Param("range") int range);

       @Modifying
       @Transactional
       @Query(value = "INSERT INTO leaderboards (user_id, points, updated_at) " +
                     "VALUES (:userId, :points, :updatedAt) " +
                     "ON CONFLICT (user_id) DO UPDATE " +
                     "SET points = :points, updated_at = :updatedAt", nativeQuery = true)
       void updateUserPoints(@Param("userId") Integer userId, @Param("points") Integer points,
                     @Param("updatedAt") Instant updatedAt);

       @Modifying
       @Transactional
       @Query(value = "UPDATE leaderboards SET rank = ranks.new_rank " +
                     "FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY points DESC) as new_rank " +
                     "FROM leaderboards) as ranks " +
                     "WHERE leaderboards.id = ranks.id", nativeQuery = true)
       void updateRanks();
}