package com.guess_game_service.service.impl;

import com.guess_game_service.dto.response.GuessResponse;
import com.guess_game_service.dto.response.UserInfoResponse;
import com.guess_game_service.exception.InsufficientTurnsException;
import com.guess_game_service.exception.UserNotFoundException;
import com.guess_game_service.model.User;
import com.guess_game_service.repository.UserRepository;
import com.guess_game_service.service.IGameService;
import com.guess_game_service.service.ILeaderboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GameServiceImpl implements IGameService {

    Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
    UserRepository userRepository;
    ILeaderboardService leaderboardService;


    @Override
    @Transactional
    @CacheEvict(value = "userInfo", key = "#username")
    public GuessResponse guess(String username, int guessNumber) {

        log.debug("User {} is guessing number: {}", username, guessNumber);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UserNotFoundException("User not found");
                });

        user = userRepository.findByIdWithLock(user.getId())
                .orElseThrow(() -> {
                    log.error("User not found with lock: {}", username);
                    return new UserNotFoundException("User not found");
                });

        if (user.getTurns() <= 0) {
            log.warn("User {} has no turns left", username);
            throw new InsufficientTurnsException("Not enough turns. Please buy more turns.");
        }

        int serverNumber = ThreadLocalRandom.current().nextInt(1, 6);
        boolean isWin = (guessNumber == serverNumber);

        user.setTurns(user.getTurns() - 1);

        String message;
        if (isWin) {
            user.setScore(user.getScore() + 1);
            message = "Congratulations! You guessed correctly!";
            leaderboardService.invalidateCache();
            logger.info("User {} won! Score: {}", username, user.getScore());
        } else {
            message = "Wrong guess. Try again!";
            logger.info("User {} lost. Server number was: {}", username, serverNumber);
        }

        userRepository.save(user);

        return GuessResponse.builder()
                .win(isWin)
                .serverNumber(serverNumber)
                .currentScore(user.getScore())
                .turnsLeft(user.getTurns())
                .message(message)
                .build();
    }

    @Override
    @CacheEvict(value = "userInfo", key = "#username")
    public void buyTurns(String username) {
        log.info("User {} is buying turns", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UserNotFoundException("User not found");
                });

        user.setTurns(user.getTurns() + 5);
        userRepository.save(user);

        logger.info("User {} bought 5 turns. Total turns: {}", username, user.getTurns());

    }

    @Override
    @Cacheable(value = "userInfo", key = "#username")
    public UserInfoResponse getUserInfo(String username) {
        log.debug("Fetching user info for: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UserNotFoundException("User not found");
                });

        log.debug("User info retrieved: username={}, score={}, turns={}",
                user.getUsername(), user.getScore(), user.getTurns());

        return UserInfoResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .score(user.getScore())
                .turns(user.getTurns())
                .build();
    }
}
