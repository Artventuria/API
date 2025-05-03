package com.artventuria.api.repository.jpa.badge;

import com.artventuria.api.domain.postgresql.BadgeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeProgressRepository extends JpaRepository<BadgeProgress, Integer> {
    List<BadgeProgress> findByUserId(Integer userId);

    List<BadgeProgress> findByBadgeId(Integer badgeId);

    Optional<BadgeProgress> findByUserIdAndBadgeId(Integer userId, Integer badgeId);

    boolean existsByUserIdAndBadgeId(Integer userId, Integer badgeId);

    @Query("SELECT bp FROM BadgeProgress bp WHERE bp.userId = ?1 AND bp.completed = true")
    List<BadgeProgress> findCompletedBadgesByUserId(Integer userId);

    @Query("SELECT COUNT(bp) FROM BadgeProgress bp WHERE bp.userId = :userId AND bp.completed = true")
    Long countByUserIdAndCompletedTrue(@Param("userId") Integer userId);
}