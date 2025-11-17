package com.guess_game_service.service;

import com.guess_game_service.dto.response.GuessResponse;
import com.guess_game_service.dto.response.UserInfoResponse;

public interface IGameService {

    GuessResponse guess(String username, int guessNumber);

    void buyTurns(String username);

    UserInfoResponse getUserInfo(String username);
}
