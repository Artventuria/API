package com.artventuria.api.repository.jpa.points;

import com.artventuria.api.domain.postgresql.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    List<PointTransaction> findByUserId(Integer userId);

    List<PointTransaction> findByArtworkId(Integer artworkId);

    List<PointTransaction> findByUserIdOrderByCreatedAtDesc(Integer userId);

    @Query("SELECT COALESCE(SUM(p.points), 0) FROM PointTransaction p WHERE p.userId = :userId")
    Integer sumPointsByUserId(@Param("userId") Integer userId);
}