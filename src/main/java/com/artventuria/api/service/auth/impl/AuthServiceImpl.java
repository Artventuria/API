package com.artventuria.api.service.auth.impl;

import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.security.JwtTokenProvider;
import com.artventuria.api.security.UserPrincipal;
import com.artventuria.api.service.auth.AuthService;
import com.artventuria.api.service.user.UserRegistrationService;
import com.artventuria.api.dto.auth.AuthResponse;
import com.artventuria.api.dto.auth.TokensResponse;
import com.artventuria.api.dto.auth.UserResponse;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.dto.auth.ExtendedUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationService userRegistrationService;

    @Autowired
    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserRegistrationService userRegistrationService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    public AuthResponse login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String userEmail = userPrincipal.getUsername();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        TokensResponse tokens = new TokensResponse(accessToken, refreshToken);
        ExtendedUserResponse userResponse = new ExtendedUserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getPoints(),
                user.getLastLogin(), user.getCreatedAt(), user.getUpdatedAt());
        return new AuthResponse(tokens, userResponse);
    }

    @Override
    @Transactional
    public AuthResponse register(User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRegistrationService.registerUser(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser, null, savedUser.getAuthorities());

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        TokensResponse tokens = new TokensResponse(accessToken, refreshToken);
        UserResponse userResponse = new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(),
                savedUser.getPoints());
        return new AuthResponse(tokens, userResponse);
    }

    @Override
    public AuthResponse refreshToken(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String userEmail = tokenProvider.getUserEmailFromJWT(token);
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        TokensResponse tokens = new TokensResponse(accessToken, refreshToken);
        ExtendedUserResponse userResponse = new ExtendedUserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getPoints(),
                user.getLastLogin(), user.getCreatedAt(), user.getUpdatedAt());
        return new AuthResponse(tokens, userResponse);
    }

    @Override
    public void logout(String token) {
        if (token != null && tokenProvider.validateToken(token)) {
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public void validateToken(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
    }
}