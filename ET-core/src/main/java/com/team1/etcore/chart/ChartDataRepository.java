package com.team1.etcore.chart;

import com.team1.etcore.chart.dto.StockResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ChartDataRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveStockData(String key, StockResponseDto stockData) {
        redisTemplate.opsForValue().set(key, stockData, 1, TimeUnit.DAYS); // 1일 동안 캐싱
    }

    public Optional<StockResponseDto> getStockData(String key) {
        StockResponseDto stockData = (StockResponseDto) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(stockData);
    }
}