package com.artventuria.api.service.auth;

import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.dto.auth.AuthResponse;

public interface AuthService {
    AuthResponse login(String email, String password);
    
    AuthResponse register(User user);
    
    AuthResponse refreshToken(String token);
    
    void logout(String token);
    
    void validateToken(String token);
}