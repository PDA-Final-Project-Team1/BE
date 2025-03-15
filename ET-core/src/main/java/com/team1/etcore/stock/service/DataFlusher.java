package com.team1.etcore.stock.service;

import com.team1.etcore.stock.dto.AskStockPriceReq;
import com.team1.etcore.stock.dto.TradeStockPriceReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataFlusher {

    private final DataBuffer dataBuffer;
    private final SseService sseService;

    /**
     * 1초마다 현재 latestDataBuffer에 저장된 "최신 데이터"를 SSE로 전송
     */
    @Scheduled(fixedRate = 400)
    public void flushData() {
        // 1) 체결가
        Map<String, TradeStockPriceReq> currentPriceMap = dataBuffer.snapshotCurrentPrice();
        for (String stockCode : currentPriceMap.keySet()) {
            TradeStockPriceReq data = currentPriceMap.get(stockCode);
            log.info(">>> 체결가 전송");
            sseService.sendStockData(stockCode, "currentPrice", data);
        }

        // 2) 호가
        Map<String, AskStockPriceReq> askBidMap = dataBuffer.snapshotAskBid();
        for (String stockCode : askBidMap.keySet()) {
            AskStockPriceReq data = askBidMap.get(stockCode);
            log.info(">>> 호가 전송");
            sseService.sendStockData(stockCode, "askBid", data);
        }
    }
}