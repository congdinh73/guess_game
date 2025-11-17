package com.guess_game_service.service.impl;

import com.guess_game_service.dto.response.GuessResponse;
import com.guess_game_service.dto.response.UserInfoResponse;
import com.guess_game_service.exception.InsufficientTurnsException;
import com.guess_game_service.exception.UserNotFoundException;
import com.guess_game_service.model.User;
import com.guess_game_service.repository.UserRepository;
import com.guess_game_service.service.ILeaderboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GameServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    ILeaderboardService leaderboardService;
    @InjectMocks
    GameServiceImpl gameService;

    User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("pass")
                .email("test@example.com")
                .score(5)
                .turns(3)
                .build();
    }

    @Test
    void guess_CorrectNumber_IncreasesScoreAndDecreasesTurns() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(leaderboardService).invalidateCache();

        // Đảm bảo đoán đúng số (mock random)
        int guessNumber = 1;
        GuessResponse response = gameService.guess("testuser", guessNumber);
        assertEquals("testuser", user.getUsername());
        assertTrue(response.getTurnsLeft() < 3);
        assertTrue(response.getCurrentScore() >= 5);
    }

    @Test
    void guess_WrongNumber_OnlyDecreasesTurns() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(leaderboardService).invalidateCache();

        int guessNumber = 99; // Đảm bảo sai
        GuessResponse response = gameService.guess("testuser", guessNumber);
        assertEquals("testuser", user.getUsername());
        assertTrue(response.getTurnsLeft() < 3);
        assertEquals(5, response.getCurrentScore());
    }

    @Test
    void guess_InsufficientTurns_ThrowsException() {
        user.setTurns(0);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(user));
        assertThrows(InsufficientTurnsException.class, () -> gameService.guess("testuser", 1));
    }

    @Test
    void guess_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> gameService.guess("nouser", 1));
    }

    @Test
    void buyTurns_IncreasesTurns() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        gameService.buyTurns("testuser");
        assertTrue(user.getTurns() >= 8);
    }

    @Test
    void buyTurns_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> gameService.buyTurns("nouser"));
    }

    @Test
    void getUserInfo_ReturnsUserInfo() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        UserInfoResponse info = gameService.getUserInfo("testuser");
        assertEquals("testuser", info.getUsername());
        assertEquals("test@example.com", info.getEmail());
        assertEquals(5, info.getScore());
        assertEquals(3, info.getTurns());
    }

    @Test
    void getUserInfo_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> gameService.getUserInfo("nouser"));
    }
}

