package com.guess_game_service.service.impl;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RateLimitService {

    Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String username) {
        return buckets.computeIfAbsent(username, this::createNewBucket);
    }

    private Bucket createNewBucket(String username) {
        // Allow 10 requests per 30 seconds
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillIntervally(10, Duration.ofSeconds(30))
                .build();

        log.debug("Created new rate limit bucket for user: {}", username);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public boolean allowRequest(String username) {
        Bucket bucket = resolveBucket(username);
        boolean consumed = bucket.tryConsume(1);

        if (!consumed) {
            log.warn("Rate limit exceeded for user: {}", username);
        }

        return consumed;
    }

    public long getAvailableTokens(String username) {
        Bucket bucket = resolveBucket(username);
        return bucket.getAvailableTokens();
    }
}
