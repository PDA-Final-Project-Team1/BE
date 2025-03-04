package com.team1.etcore.trade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeConsumer {

    private final TradeService tradeService;

    @KafkaListener(topics = "H0STCNT0", groupId = "etcore-trade-group")
    public void consumeTradeMessage(String message) {
        try {
            String[] parts = message.split("\\^");
            if (parts.length < 2) {
                log.warn("유효하지 않은 메시지 포맷: {}", message);
                return;
            }
            String stockCode = parts[0];
            BigDecimal tradePrice = new BigDecimal(parts[1]);
            log.info("체결 데이터 수신: 종목코드={}, 체결가={}", stockCode, tradePrice);
            tradeService.processTrade(stockCode, tradePrice);
        } catch (Exception e) {
            log.error("체결 메시지 처리 중 오류 발생: {} / {}", message, e.getMessage(), e);
        }
    }
}
