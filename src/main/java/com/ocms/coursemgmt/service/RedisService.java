package com.ocms.coursemgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final long REFRESH_TTL = 7;

    public void saveRefreshToken(Long userId, String deviceId, String token){
        String Key = userId + ":" + deviceId;
        redisTemplate.opsForValue().set(Key,token,REFRESH_TTL, TimeUnit.DAYS);
    }

    public String getRefreshToken(Long userId, String deviceId){
       return redisTemplate.opsForValue().get(userId + ":" + deviceId);
    }

    public void deleteRefreshToken(Long userId, String deviceId){
        redisTemplate.delete(userId + ":" + deviceId);
    }

    public void deleteAllUserSessions(Long userId){
        Set<String> keys = redisTemplate.keys(userId + ":*");
        if(keys != null){
            redisTemplate.delete(keys);
        }
    }

    public Set<String> getUserSessions(Long userId){
        Set<String> keys = redisTemplate.keys(userId + ":*");
        if(keys == null){
            return Set.of();
        }
        return keys.stream().map(key -> key.split(":")[1]).collect(Collectors.toSet());

    }

}
