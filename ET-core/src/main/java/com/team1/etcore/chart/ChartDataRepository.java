package com.team1.etcore.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etcore.chart.dto.StockResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ChartDataRepository {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveStockData(String key, StockResponseDto stockData) {
        try {
            String jsonData = objectMapper.writeValueAsString(stockData);
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set(key, jsonData, 1, TimeUnit.DAYS); // 1일 동안 캐싱
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<StockResponseDto> getStockData(String key) {
        try {
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            String jsonData = ops.get(key);
            if (jsonData != null) {
                return Optional.of(objectMapper.readValue(jsonData, StockResponseDto.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
