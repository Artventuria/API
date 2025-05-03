package com.artventuria.api.service.auth;

import com.artventuria.api.dto.auth.PasswordResetVerifyResponse;

public interface IPasswordResetService {
    void requestPasswordReset(String email);
    PasswordResetVerifyResponse verifyPasswordResetToken(String token);
    void resetPassword(String token, String newPassword);
}