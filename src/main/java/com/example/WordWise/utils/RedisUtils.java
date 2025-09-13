package com.example.WordWise.utils;

import com.example.WordWise.enums.PracticeSTOMPCommunicationEnum;
import com.example.WordWise.model.redis_object.CreatePracticeRoomRedisObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean filterToHandShake(String userId, String channel, String action) {
        PracticeSTOMPCommunicationEnum actionEnum = PracticeSTOMPCommunicationEnum.fromKey(action);

        switch (actionEnum) {
            case CREATE_ROOM:
                return filterToHandShakeForAdminRoom(userId, channel);
            case REQUEST_JOIN:
                return filterToHandShakeForRequestRoom(userId, channel);
            case PERSONAL_ROOM:
                return filterToHandShakeForPersonalRoom(userId, channel);
            default:
                return true;
        }
    }

    public Boolean filterToHandShakeForAdminRoom(String userId, String channel) {
        CreatePracticeRoomRedisObject redisObject = (CreatePracticeRoomRedisObject) this.get(channel);

        if (redisObject == null) {
            return false;
        }

        if (redisObject.getAllowedList() != null && !redisObject.getAllowedList().contains(userId)) {
            return false;
        }

        return true;
    }

    public Boolean filterToHandShakeForRequestRoom(String userId, String channel) {
        List<String> userIds = (List<String>) get(channel);

        if (userIds == null || userIds.isEmpty() || !userIds.contains(userId)) {
            return false;
        }

        return true;
    }

    public Boolean filterToHandShakeForPersonalRoom(String userId, String channel) {
        return channel.contains(userId);
    }

}
