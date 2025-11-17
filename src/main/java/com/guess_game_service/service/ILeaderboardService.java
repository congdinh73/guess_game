package com.guess_game_service.service;

import com.guess_game_service.dto.response.LeaderboardResponse;

import java.util.List;

public interface ILeaderboardService {

    List<LeaderboardResponse> getTop10();

    void invalidateCache();
}
