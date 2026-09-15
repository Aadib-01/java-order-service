package com.example.demo;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String KEY_PREFIX = "rate-limit:";
    private static final int MAX_REQUESTS = 10;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    public boolean isAllowed(String clientId) {
        String key = KEY_PREFIX + clientId;

        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount != null && currentCount == 1L) {
            redisTemplate.expire(key, WINDOW);
        }

        return currentCount != null && currentCount <= MAX_REQUESTS;
    }
}
