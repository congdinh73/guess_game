package com.guess_game_service.service.impl;

import com.guess_game_service.dto.response.LeaderboardResponse;
import com.guess_game_service.repository.UserRepository;
import com.guess_game_service.service.ILeaderboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LeaderboardServiceImpl implements ILeaderboardService {

    UserRepository userRepository;

    @Override
    @Cacheable(value = "leaderboard", key = "'top10'")
    public List<LeaderboardResponse> getTop10() {
        log.info("Fetching top 10 leaderboard from database");

        long startTime = System.currentTimeMillis();

        Pageable pageable = PageRequest.of(0, 10);

        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardResponse> leaderboard = userRepository.findTop10ByOrderByScoreDesc(pageable)
                .stream()
                .map(user -> {
                    log.debug("Rank {}: {} with score {}", rank.get(), user.getUsername(), user.getScore());
                    return LeaderboardResponse.builder()
                            .rank(rank.getAndIncrement())
                            .username(user.getUsername())
                            .score(user.getScore())
                            .build();
                })
                .collect(Collectors.toList());

        long duration = System.currentTimeMillis() - startTime;
        log.info("Leaderboard fetched successfully. {} users in {}ms", leaderboard.size(), duration);

        return leaderboard;
    }

    @Override
    @CacheEvict(value = "leaderboard", allEntries = true)
    public void invalidateCache() {
        log.info("Leaderboard cache invalidated due to score update");

    }
}
