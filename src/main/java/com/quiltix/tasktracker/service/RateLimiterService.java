package com.quiltix.tasktracker.service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final RedisTemplate<String,String> redisTemplate;

    public RateLimiterService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryAcquire(String email){
        String key = "passwordResetRate:" + email;

        Boolean success = redisTemplate.opsForValue().setIfAbsent(key,"1",60, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }
}
