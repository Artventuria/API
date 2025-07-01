package com.artventuria.api.service.leaderboard.impl;

import com.artventuria.api.domain.postgresql.LeaderboardEntry;
import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.repository.jpa.points.LeaderboardRepository;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.service.badge.BadgeProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceImplTest {

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BadgeProgressService badgeProgressService;

    @InjectMocks
    private LeaderboardServiceImpl leaderboardService;

    private LeaderboardEntry entry1;
    private LeaderboardEntry entry2;
    private User user1;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1);
        user1.setUsername("user1");

        entry1 = new LeaderboardEntry();
        entry1.setUserId(1);
        entry1.setPoints(100);
        entry1.setRank(1);

        entry2 = new LeaderboardEntry();
        entry2.setUserId(2);
        entry2.setPoints(50);
        entry2.setRank(2);
    }

    @Test
    void getLeaderboard_shouldReturnPopulatedLeaderboard() {
        // Arrange
        when(leaderboardRepository.findAllByOrderByPointsDescRankAsc(anyInt(), anyInt())).thenReturn(List.of(entry1, entry2));
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        // Assume user 2 is not found, to test robustness
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        when(badgeProgressService.getCompletedBadges(anyInt())).thenReturn(Collections.emptyList());

        // Act
        List<LeaderboardEntry> leaderboard = leaderboardService.getLeaderboard(10, 0);

        // Assert
        assertNotNull(leaderboard);
        assertEquals(2, leaderboard.size());
        assertEquals("user1", leaderboard.get(0).getUsername());
        assertNull(leaderboard.get(1).getUsername()); // User 2 not found
        verify(leaderboardRepository).findAllByOrderByPointsDescRankAsc(10, 0);
        verify(userRepository, times(2)).findById(anyInt());
    }

    @Test
    void getUserRank_shouldReturnPopulatedUserRank() {
        // Arrange
        when(leaderboardRepository.findByUserId(1)).thenReturn(Optional.of(entry1));
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(badgeProgressService.getCompletedBadges(1)).thenReturn(Collections.emptyList());

        // Act
        LeaderboardEntry userRank = leaderboardService.getUserRank(1);

        // Assert
        assertNotNull(userRank);
        assertEquals("user1", userRank.getUsername());
        verify(leaderboardRepository).findByUserId(1);
        verify(userRepository).findById(1);
    }

    @Test
    void getUserRank_shouldThrowException_whenUserNotInLeaderboard() {
        // Arrange
        when(leaderboardRepository.findByUserId(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            leaderboardService.getUserRank(1);
        });
        verify(leaderboardRepository).findByUserId(1);
    }
}
