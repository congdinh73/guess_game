package com.guess_game_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LeaderboardResponse {

    private int rank;
    private String username;
    private int score;
}
