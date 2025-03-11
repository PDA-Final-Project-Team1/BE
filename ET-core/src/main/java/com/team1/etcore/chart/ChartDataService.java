
package com.team1.etcore.chart;

import com.team1.etcore.chart.dto.StockRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ChartDataService {
    private final RestTemplate restTemplate;
    private final ChartDataRepository chartDataRepository;

    public StockRes getStockData(String stockCode) {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = stockCode + ":" + today;

        // Redis에서 데이터 조회
        return chartDataRepository.getStockData(redisKey)
                .orElseGet(() -> fetchAndCacheStockData(stockCode, redisKey));
    }

    private StockRes fetchAndCacheStockData(String stockCode, String redisKey) {
        String apiUrl = "https://api.stock.naver.com/chart/domestic/item/" + stockCode + "?periodType=dayCandle";
        StockRes stockData = restTemplate.getForObject(apiUrl, StockRes.class);

        if (stockData != null) {
            chartDataRepository.saveStockData(redisKey, stockData);
        }

        return stockData;
    }
}
