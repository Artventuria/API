package com.artventuria.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.artventuria.api.service.auth.AuthService;
import com.artventuria.api.service.auth.IPasswordResetService;
import com.artventuria.api.dto.auth.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final IPasswordResetService passwordResetService;
    private final String mobileDeeplink;

    public AuthController(AuthService authService, IPasswordResetService passwordResetService,
            @Value("${mobile.deeplink.reset-password}") String mobileDeeplink) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
        this.mobileDeeplink = mobileDeeplink;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request.toUser());
        return ResponseEntity.ok(authService.login(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            authService.logout(token.substring(7));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            authService.validateToken(token.substring(7));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password/verify")
    public ResponseEntity<PasswordResetVerifyResponse> verifyPasswordResetToken(
            @Valid @RequestBody PasswordResetVerifyRequest request) {
        return ResponseEntity.ok(passwordResetService.verifyPasswordResetToken(request.getToken()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody PasswordResetConfirmRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to redirect to mobile app with reset token using deeplink.
     * Verifies if the token is valid before redirecting to the mobile app.
     * 
     * @param token    The reset token
     * @param response The HTTP response
     * @return A redirection to the mobile app
     * @throws IOException In case of error during redirection
     */
    @GetMapping("/reset-redirect")
    public void resetRedirect(@RequestParam String token, HttpServletResponse response) throws IOException {
        try {
            // Verify if the token exists and is valid to prevent abuse
            passwordResetService.verifyPasswordResetToken(token);

            // If the token is valid, redirect to the mobile app
            response.sendRedirect(mobileDeeplink + "?token=" + token);
        } catch (Exception e) {
            // In case of invalid or expired token, redirect to an error page
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or expired token");
        }
    }
}

class ForgotPasswordRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}