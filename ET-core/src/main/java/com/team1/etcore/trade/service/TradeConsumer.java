package com.team1.etcore.trade.service;

import com.team1.etcore.trade.dto.Position;
import com.team1.etcore.trade.dto.QuoteReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeConsumer {

    private final TradeService tradeService;

    @KafkaListener(topics = "H0STASP0", groupId = "etcore-trade-group")
    public void consumeTradeMessage(ConsumerRecord<String, String> record) {
        try {
            String stockCode = record.key();
            String value = record.value();

            String[] quoteData = value.split("\\^");
            if (quoteData.length < 42) {
                log.warn("필드 개수가 부족합니다: {}", value);
                return;
            }

            QuoteReq quoteReq = QuoteReq.builder().
                    stockCode(stockCode)
                    .buyPrice(new BigDecimal(quoteData[2]))
                    .buyAmount(new BigDecimal(quoteData[22]))
                    .sellPrice(new BigDecimal(quoteData[12]))
                    .sellAmount(new BigDecimal(quoteData[32]))
                    .build();

            // 체결 처리
            processTrade(quoteReq);
        } catch (Exception e) {
            log.error("Kafka 메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    public void processTrade(QuoteReq quoteReq) {
        try {
            // 매수 주문 처리
            tradeService.processMatching(Position.BUY, quoteReq.getStockCode(),
                    quoteReq.getBuyPrice(), quoteReq.getBuyAmount());

            // 매도 주문 처리
            tradeService.processMatching(Position.SELL, quoteReq.getStockCode(),
                    quoteReq.getSellPrice(), quoteReq.getSellAmount());
        } catch (Exception e) {
            log.error("체결 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("체결 처리 실패", e);
        }
    }
}
