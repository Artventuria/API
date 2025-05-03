package com.artventuria.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AdminAuthenticationFilter extends OncePerRequestFilter {

    private static final String ADMIN_PATH_PREFIX = "/api/admin/";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (!path.startsWith(ADMIN_PATH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if there's an authentication in the context (from JWT filter) with
        // admin role
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User is authenticated with admin role via JWT
            filterChain.doFilter(request, response);
            return;
        }

        // If no JWT authentication with admin role, return forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}