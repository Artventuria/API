package com.artventuria.api.controller;

import com.artventuria.api.domain.postgresql.LeaderboardEntry;
import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.dto.leaderboard.UserRankResponse;
import com.artventuria.api.service.leaderboard.LeaderboardService;
import com.artventuria.api.service.user.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final UserServiceImpl userService;

    @Autowired
    public LeaderboardController(LeaderboardService leaderboardService, UserServiceImpl userService) {
        this.leaderboardService = leaderboardService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserRankResponse>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        List<LeaderboardEntry> entries = leaderboardService.getLeaderboard(limit, offset);
        List<UserRankResponse> response = entries.stream()
                .map(this::convertToUserRankResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserRankResponse> getCurrentUserRank() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        
        LeaderboardEntry entry = leaderboardService.getUserRank(user.getId());
        return ResponseEntity.ok(convertToUserRankResponse(entry));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<UserRankResponse>> getNearbyUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        
        List<LeaderboardEntry> entries = leaderboardService.getNearbyUsers(user.getId());
        List<UserRankResponse> response = entries.stream()
                .map(this::convertToUserRankResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private UserRankResponse convertToUserRankResponse(LeaderboardEntry entry) {
        return new UserRankResponse(
                entry.getUserId() != null ? entry.getUserId().longValue() : null,
                entry.getUsername(),
                entry.getRank() != 0 ? (long) entry.getRank() : null,
                entry.getPoints(),
                entry.getBadgeCount()
        );
    }
}