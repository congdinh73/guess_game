package com.guess_game_service.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitServiceTest {
    RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService();
    }

    @Test
    void allowRequest_LimitsRequests() {
        String username = "user1";
        int allowed = 0;
        for (int i = 0; i < 15; i++) {
            if (rateLimitService.allowRequest(username)) allowed++;
        }
        assertTrue(allowed <= 10); // Theo cấu hình mặc định
    }

    @Test
    void getAvailableTokens_ReturnsNonNegative() {
        String username = "user2";
        long tokens = rateLimitService.getAvailableTokens(username);
        assertTrue(tokens >= 0);
    }
}

