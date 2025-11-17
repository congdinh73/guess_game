package com.guess_game_service.service.impl;

import com.guess_game_service.dto.request.LoginRequest;
import com.guess_game_service.dto.request.RegisterRequest;
import com.guess_game_service.dto.response.AuthResponse;
import com.guess_game_service.exception.UserAlreadyExistsException;
import com.guess_game_service.model.User;
import com.guess_game_service.repository.UserRepository;
import com.guess_game_service.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock AuthenticationManager authenticationManager;
    @Mock UserDetailsService userDetailsService;
    @InjectMocks AuthServiceImpl authService;

    User user;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("pass")
                .email("test@example.com")
                .score(0)
                .turns(5)
                .build();
    }

    @Test
    void register_NewUser_Success() {
        RegisterRequest req = RegisterRequest.builder()
                .username("testuser")
                .password("pass")
                .email("test@example.com")
                .build();
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token");
        AuthResponse resp = authService.register(req);
        assertEquals("token", resp.getToken());
        assertEquals("User registered successfully", resp.getMessage());
    }

    @Test
    void register_ExistingUser_ThrowsException() {
        RegisterRequest req = RegisterRequest.builder()
                .username("testuser")
                .password("pass")
                .email("test@example.com")
                .build();
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(req));
    }

    @Test
    void login_Success() {
        LoginRequest req = LoginRequest.builder()
                .username("testuser")
                .password("pass")
                .build();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token");
        when(authenticationManager.authenticate(any())).thenReturn(mock(org.springframework.security.core.Authentication.class));
        AuthResponse resp = authService.login(req);
        assertEquals("token", resp.getToken());
        assertEquals("Login successful", resp.getMessage());
    }
}
