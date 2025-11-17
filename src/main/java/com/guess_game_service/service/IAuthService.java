package com.guess_game_service.service;

import com.guess_game_service.dto.request.LoginRequest;
import com.guess_game_service.dto.request.RegisterRequest;
import com.guess_game_service.dto.response.AuthResponse;

public interface IAuthService {


    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
