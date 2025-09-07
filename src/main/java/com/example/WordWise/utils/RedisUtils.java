package com.example.WordWise.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Add key-value to Redis with optional expiration
     *
     * @param key   Redis key
     * @param value Value to store
     * @param ttl   Time to live in seconds. 0 means no expiration
     */
    public void set(String key, Object value, long ttl) {
        redisTemplate.opsForValue().set(key, value);
        if (ttl > 0) {
            redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        }
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
