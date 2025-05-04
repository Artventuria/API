package com.artventuria.api.dto.leaderboard;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRankResponse {
    private Long userId;
    private String username;
    private Long rank;
    private int points;
    private int badgeCount;
}