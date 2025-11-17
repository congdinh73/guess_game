package com.guess_game_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GuessRequest {

    @NotNull(message = "Guess number is required")
    @Min(value = 1, message = "Guess number must be between 1 and 5")
    @Max(value = 5, message = "Guess number must be between 1 and 5")
    private Integer guessNumber;
}
