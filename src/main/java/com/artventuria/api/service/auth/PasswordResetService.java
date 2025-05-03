package com.artventuria.api.service.auth;

import com.artventuria.api.domain.postgresql.PasswordResetToken;
import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.repository.jpa.security.PasswordResetTokenRepository;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.service.email.EmailService;
import com.artventuria.api.dto.auth.PasswordResetVerifyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService implements IPasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationContext applicationContext;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PasswordResetService.class);

    @Override
    public void requestPasswordReset(String email) {
        log.info("Starting password reset request for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("Password reset requested for non-existent email: {}", email);
                    return null;
                });

        if (user == null) {
            return;
        }

        log.debug("Found user with ID: {} for email: {}", user.getId(), email);

        // Check if there's an existing valid token
        Optional<PasswordResetToken> existingToken = passwordResetTokenRepository
                .findByUserIdAndUsedFalseAndExpiresAtAfter(user.getId(), Instant.now());

        if (existingToken.isPresent()) {
            log.info("Valid reset token already exists for email: {}", email);
            return;
        }

        // Get proxy to call transactional method
        PasswordResetService proxy = applicationContext.getBean(PasswordResetService.class);
        proxy.createAndSaveToken(user, email);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createAndSaveToken(User user, String email) {
        String token = UUID.randomUUID().toString();
        Instant now = Instant.now();

        try {
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setUser(user);
            passwordResetToken.setToken(token);
            passwordResetToken.setExpiresAt(now.plus(24, ChronoUnit.HOURS));
            passwordResetToken.setUsed(false);
            passwordResetToken.setCreatedAt(now);
            passwordResetToken.setUpdatedAt(now);

            PasswordResetToken savedToken = passwordResetTokenRepository.save(passwordResetToken);

            if (savedToken == null || savedToken.getId() == null) {
                log.error("Failed to save token - no ID generated");
                return;
            }

            // Move sending the email after the transaction to avoid rollback issues
            String finalToken = token;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        emailService.sendPasswordResetEmail(email, finalToken);
                        log.info("Password reset email sent to: {}", email);
                    } catch (Exception e) {
                        log.error("Failed to send password reset email to {}: {}", email, e.getMessage());
                        // Do not propagate exception to avoid rollback
                    }
                }
            });
        } catch (Exception e) {
            log.error("Failed to create password reset token: {}", e.getMessage());
            // Do not propagate exception to avoid status 500
        }
    }

    @Override
    public PasswordResetVerifyResponse verifyPasswordResetToken(String token) {
        Optional<PasswordResetToken> resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken.isEmpty()) {
            return new PasswordResetVerifyResponse(false, "Invalid token");
        }

        if (resetToken.get().getUsed() || resetToken.get().getExpiresAt().isBefore(Instant.now())) {
            return new PasswordResetVerifyResponse(false, "Token has expired or has been used");
        }

        return new PasswordResetVerifyResponse(true, "Token is valid");
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (resetToken.getUsed() || resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token has expired or has been used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    public Optional<User> getUserByToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .filter(resetToken -> !resetToken.getUsed() && !resetToken.getExpiresAt().isBefore(Instant.now()))
                .map(PasswordResetToken::getUser);
    }
}