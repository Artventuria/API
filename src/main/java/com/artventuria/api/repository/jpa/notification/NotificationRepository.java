package com.artventuria.api.repository.jpa.notification;

import com.artventuria.api.domain.postgresql.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Notification> findByUserIdAndReadOrderByCreatedAtDesc(Integer userId, boolean read);

    List<Notification> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Integer userId,
            Instant startDate,
            Instant endDate);

    Page<Notification> findByUserId(Integer userId, Pageable pageable);

    List<Notification> findByType(String type);

    List<Notification> findByUserIdAndReadFalse(Integer userId);

    Page<Notification> findByUserIdAndReadFalse(Integer userId, Pageable pageable);

    Long countByUserIdAndReadFalse(Integer userId);

    Page<Notification> findByUserIdAndType(Integer userId, String type, Pageable pageable);
}