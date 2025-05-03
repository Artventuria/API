package com.artventuria.api.dto.auth;

public class AuthResponse {
    private TokensResponse tokens;
    private UserResponse user;

    public AuthResponse() {}

    public AuthResponse(TokensResponse tokens, UserResponse user) {
        this.tokens = tokens;
        this.user = user;
    }

    public TokensResponse getTokens() {
        return tokens;
    }

    public void setTokens(TokensResponse tokens) {
        this.tokens = tokens;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}