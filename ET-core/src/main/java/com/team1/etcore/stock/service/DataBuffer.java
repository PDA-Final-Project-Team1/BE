package com.team1.etcore.stock.service;

import com.team1.etcore.stock.dto.AskStockPriceReq;
import com.team1.etcore.stock.dto.TradeStockPriceReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DataBuffer {

    // 체결가: 종목코드 -> 마지막 TradeStockPriceReq
    private final Map<String, TradeStockPriceReq> currentPriceMap = new ConcurrentHashMap<>();

    // 호가: 종목코드 -> 마지막 AskStockPriceReq
    private final Map<String, AskStockPriceReq> askBidMap = new ConcurrentHashMap<>();

    // 체결가 최신 데이터 갱신
    public void updateCurrentPrice(String stockCode, TradeStockPriceReq data) {
        currentPriceMap.put(stockCode, data);
    }

    // 호가 최신 데이터 갱신
    public void updateAskBid(String stockCode, AskStockPriceReq data) {
        askBidMap.put(stockCode, data);
    }

    // 모든 최신 체결가 데이터를 스냅샷으로 가져오고, 맵은 그대로 유지(덮어쓰기로 계속 최신화)
    public Map<String, TradeStockPriceReq> snapshotCurrentPrice() {
        // ConcurrentHashMap을 그대로 반환하거나, 복사본을 만들 수도 있음
        return new ConcurrentHashMap<>(currentPriceMap);
    }

    public Map<String, AskStockPriceReq> snapshotAskBid() {
        return new ConcurrentHashMap<>(askBidMap);
    }
}
