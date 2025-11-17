package com.guess_game_service.controller;

import com.guess_game_service.dto.request.GuessRequest;
import com.guess_game_service.dto.response.ErrorResponse;
import com.guess_game_service.dto.response.GuessResponse;
import com.guess_game_service.dto.response.LeaderboardResponse;
import com.guess_game_service.dto.response.UserInfoResponse;
import com.guess_game_service.service.IGameService;
import com.guess_game_service.service.ILeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Game", description = "Game APIs - Guessing numbers from 1 to 5")
@SecurityRequirement(name = "Bearer Auth")
@Slf4j
public class GameController {

    IGameService gameService;
    ILeaderboardService leaderboardService;

    @PostMapping("/guess")
    @Operation(
            summary = "Guess a number",
            description = "Guess a number between 1-5. Costs 1 turn. Win = +1 score.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Guess result (win or lose)",
                    content = @Content(schema = @Schema(implementation = GuessResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid guess or insufficient turns",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<GuessResponse> guess(
            @Valid @RequestBody GuessRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Guess request: {}", request);
        GuessResponse response = gameService.guess(userDetails.getUsername(), request.getGuessNumber());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/buy-turns")
    @Operation(
            summary = "Buy turns",
            description = "Purchase 5 additional turns to play. Free for testing."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Turns purchased successfully"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> buyTurns(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Buy turns request: {}", userDetails.getUsername());
        gameService.buyTurns(userDetails.getUsername());
        return ResponseEntity.ok("Successfully purchased 5 turns");
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get my info",
            description = "Get current user's info (username, email, score, turns)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User info retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = UserInfoResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get current user's info: {}", userDetails.getUsername());
        UserInfoResponse response = gameService.getUserInfo(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/leaderboard")
    @Operation(
            summary = "Get leaderboard",
            description = "Get top 10 players with highest scores"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Leaderboard retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = LeaderboardResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<LeaderboardResponse>> getLeaderboard() {
        log.info("Get leaderboard");
        List<LeaderboardResponse> leaderboard = leaderboardService.getTop10();
        return ResponseEntity.ok(leaderboard);
    }
}
