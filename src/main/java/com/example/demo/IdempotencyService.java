package com.example.demo;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "idempotency:";
    private static final Duration TTL = Duration.ofHours(24);

    public Optional<OrderResponse> getExistingResponse(String idempotencyKey) {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + idempotencyKey);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, OrderResponse.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void saveResponse(String idempotencyKey, OrderResponse response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(KEY_PREFIX + idempotencyKey, json, TTL);
        } catch (Exception e) {
            e.printStackTrace(); // temporary - so we can SEE if this fails again
        }
    }
}