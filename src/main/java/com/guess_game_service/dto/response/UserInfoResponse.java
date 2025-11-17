package com.guess_game_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserInfoResponse {

    private String username;
    private String email;
    private int score;
    private int turns;
}
