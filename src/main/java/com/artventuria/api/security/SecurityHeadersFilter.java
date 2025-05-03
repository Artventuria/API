package com.artventuria.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityHeadersFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        logger.debug("Adding security headers for path: {}", request.getRequestURI());

        // XSS protection
        response.setHeader("X-XSS-Protection", "1; mode=block");
        // Clickjacking protection
        response.setHeader("X-Frame-Options", "DENY");
        // MIME sniffing protection
        response.setHeader("X-Content-Type-Options", "nosniff");
        // Content Security Policy
        response.setHeader("Content-Security-Policy", "default-src 'self'");
        // Referrer Policy
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        // HSTS (force HTTPS)
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        logger.debug("Security headers added successfully for path: {}", request.getRequestURI());

        filterChain.doFilter(request, response);
    }
}