package com.team1.etpipeline.redis;

import com.team1.etpipeline.redis.dto.StockResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChartDataService {

    private final RestTemplate restTemplate;
    private final ChartDataRepository chartDataRepository;

    public StockResponseDto getStockData(String stockId) {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = stockId + ":" + today;

        // Redis에서 데이터 조회
        Optional<StockResponseDto> cachedData = chartDataRepository.getStockData(redisKey);
        if (cachedData.isPresent()) {
            return cachedData.get();
        }

        // Redis에 없으면 외부 API 호출
        String apiUrl = "https://api.stock.naver.com/chart/domestic/item/" + stockId + "?periodType=dayCandle";
        StockResponseDto stockData = restTemplate.getForObject(apiUrl, StockResponseDto.class);

        if (stockData != null) {
            // 가져온 데이터를 Redis에 저장
            chartDataRepository.saveStockData(redisKey, stockData);
        }

        return stockData;
    }
}
