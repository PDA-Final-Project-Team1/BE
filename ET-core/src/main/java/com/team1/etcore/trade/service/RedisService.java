package com.team1.etcore.trade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Long> redisTemplate;

    public boolean saveDuplicateKey(Long id) {
        String redisKey = buildDuplicateKey(id);
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(redisKey, id));
    }

    private String buildDuplicateKey(Long id) {
        return "processed:trade:" + id;
    }
}
