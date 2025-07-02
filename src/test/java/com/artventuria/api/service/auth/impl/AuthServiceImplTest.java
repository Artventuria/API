package com.artventuria.api.service.auth.impl;

import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.security.JwtTokenProvider;
import com.artventuria.api.service.user.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private JwtTokenProvider tokenProvider; // Mocked but not used in register logic

    @Mock
    private AuthenticationManager authenticationManager; // Mocked but not used in register logic

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password123");
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(user);
        });

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(user);
        });

        assertEquals("Username already taken", exception.getMessage());
    }

    @Test
    void register_shouldReturnAuthResponse_whenRegistrationIsSuccessful() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRegistrationService.registerUser(any(User.class))).thenReturn(user);
        when(tokenProvider.generateToken(any())).thenReturn("accessToken");
        when(tokenProvider.generateRefreshToken(any())).thenReturn("refreshToken");

        // When
        var authResponse = authService.register(user);

        // Then
        assertNotNull(authResponse);
        assertNotNull(authResponse.getTokens());
        assertEquals("accessToken", authResponse.getTokens().getAccess_token());
        assertEquals("refreshToken", authResponse.getTokens().getRefresh_token());
        assertNotNull(authResponse.getUser());
        assertEquals("test@example.com", authResponse.getUser().getEmail());
    }
}
