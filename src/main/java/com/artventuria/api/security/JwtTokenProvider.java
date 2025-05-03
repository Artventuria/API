package com.artventuria.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;

    @Value("${jwt.refresh-expiration}")
    private int refreshTokenExpirationMs;

    private final Key signingKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") int jwtExpirationInMs,
            @Value("${jwt.refresh-expiration}") int refreshTokenExpirationMs) {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret cannot be null or empty");
        }
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMs = jwtExpirationInMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        // Ensure the key is at least 256 bits (32 bytes) for HS256
        byte[] keyBytes = new byte[32];
        byte[] secretBytes = jwtSecret.getBytes();
        System.arraycopy(secretBytes, 0, keyBytes, 0, Math.min(secretBytes.length, keyBytes.length));
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getSigningKey() {
        return this.signingKey;
    }

    public String generateToken(Authentication authentication) {
        org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication
                .getPrincipal();
        String userEmail = userDetails.getUsername(); // In our case, username is the email

        // Extract roles from authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.startsWith("ROLE_") ? authority.substring(5) : authority)
                .collect(Collectors.toList());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(userEmail)
                .claim("email", userEmail)
                .claim("type", "access")
                .claim("roles", roles) // Add roles to the token
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication
                .getPrincipal();
        String userEmail = userDetails.getUsername(); // In our case, username is the email

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .subject(userEmail)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUserEmailFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException ex) {
            // Invalid JWT signature
            return false;
        } catch (MalformedJwtException ex) {
            // Invalid JWT token
            return false;
        } catch (ExpiredJwtException ex) {
            // Expired JWT token
            return false;
        } catch (UnsupportedJwtException ex) {
            // Unsupported JWT token
            return false;
        } catch (IllegalArgumentException ex) {
            // JWT claims string is empty
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("email", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("roles", List.class);
    }
}