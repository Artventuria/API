package com.artventuria.api.repository.jpa.security;

import com.artventuria.api.domain.postgresql.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserIdAndUsedFalseAndExpiresAtAfter(Integer userId, Instant now);

    void deleteByExpiresAtBefore(Instant now);

    List<PasswordResetToken> findAllByUserId(Integer userId);
}