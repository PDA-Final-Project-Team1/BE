package com.team1.etcore.chart;

import com.team1.etcore.chart.dto.StockRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ChartDataRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveStockData(String key, StockRes stockData) {
        redisTemplate.opsForValue().set(key, stockData, 1, TimeUnit.DAYS); // 1일 동안 캐싱
    }

    public Optional<StockRes> getStockData(String key) {
        StockRes stockData = (StockRes) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(stockData);
    }
}