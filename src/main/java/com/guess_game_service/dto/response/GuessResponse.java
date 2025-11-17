package com.guess_game_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GuessResponse {

    private boolean win;
    private int serverNumber;
    private int currentScore;
    private int turnsLeft;
    private String message;


}
