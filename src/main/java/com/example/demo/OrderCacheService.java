package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "order-cache:";
    private static final Duration TTL = Duration.ofMinutes(5);

    public OrderResponse getCachedOrder(UUID id) {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + id);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, OrderResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void cacheOrder(UUID id, OrderResponse response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(KEY_PREFIX + id, json, TTL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void evictOrder(UUID id) {
        redisTemplate.delete(KEY_PREFIX + id);
    }
}