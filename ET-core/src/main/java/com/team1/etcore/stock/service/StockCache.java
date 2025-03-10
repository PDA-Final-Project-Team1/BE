package com.team1.etcore.stock.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etcore.stock.util.StockData;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Getter
public class StockCache {
    private final List<StockData> stockList = new ArrayList<>();

    @PostConstruct
    public void loadStockData() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/stock/StockData.json")) {
            if (inputStream == null) {
                throw new IOException("StockData.json 을 찾을 수 없습니다.");
            }
            stockList.addAll(objectMapper.readValue(inputStream, new TypeReference<>() {}));
            log.info("Stock 데이터를 성공적으로 로드했습니다. 총 개수: {}", stockList.size());
        } catch (IOException e) {
            log.error("Stock 데이터를 로드하는 중 오류 발생", e);
            throw new RuntimeException("Stock 데이터를 로드하는 중 오류 발생", e);
        }
    }
}

