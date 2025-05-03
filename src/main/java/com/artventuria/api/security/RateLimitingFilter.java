package com.artventuria.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    private final RateLimiter rateLimiter;

    public RateLimitingFilter() {
        // Configure rate limiter for 100 requests per second, matching Go API
        this.rateLimiter = RateLimiter.create(100.0);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        logger.debug("Checking rate limit for path: {}", request.getRequestURI());

        if (!rateLimiter.tryAcquire(1, TimeUnit.SECONDS)) {
            logger.warn("Rate limit exceeded for IP: {}", request.getRemoteAddr());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
            return;
        }

        logger.debug("Rate limit check passed for IP: {}", request.getRemoteAddr());
        filterChain.doFilter(request, response);
    }
}