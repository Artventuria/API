package com.artventuria.api.repository.jpa.badge;

import com.artventuria.api.domain.postgresql.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    List<Badge> findByCriteriaContaining(String locationCriteria);

    /**
     * Count the number of badges assigned to a specific user
     * 
     * @param userId ID of the user
     * @return Number of badges
     */
    @Query(value = "SELECT COUNT(*) FROM user_badges WHERE user_id = :userId", nativeQuery = true)
    Integer countBadgesByUserId(@Param("userId") Integer userId);
}