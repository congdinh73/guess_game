package com.guess_game_service.service.impl;

import com.guess_game_service.dto.response.LeaderboardResponse;
import com.guess_game_service.model.User;
import com.guess_game_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LeaderboardServiceImplTest {
    @Mock UserRepository userRepository;
    @InjectMocks LeaderboardServiceImpl leaderboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTop10_ReturnsLeaderboard() {
        User u1 = User.builder().username("a").score(10).build();
        User u2 = User.builder().username("b").score(8).build();
        when(userRepository.findTop10ByOrderByScoreDesc(any(Pageable.class))).thenReturn(Arrays.asList(u1, u2));
        List<LeaderboardResponse> result = leaderboardService.getTop10();
        assertEquals(2, result.size());
        assertEquals("a", result.get(0).getUsername());
        assertEquals(10, result.get(0).getScore());
        assertEquals(1, result.get(0).getRank());
    }

    @Test
    void invalidateCache_DoesNotThrow() {
        assertDoesNotThrow(() -> leaderboardService.invalidateCache());
    }
}
