package com.artventuria.api.repository.jpa.notification;

import com.artventuria.api.domain.postgresql.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("notificationPreferenceJpaRepository")
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Integer> {
    Optional<NotificationPreference> findByUserId(Integer userId);

    boolean existsByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}