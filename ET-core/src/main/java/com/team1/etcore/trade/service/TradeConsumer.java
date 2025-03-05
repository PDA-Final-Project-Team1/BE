package com.team1.etcore.trade.service;

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
            String data = record.value();
            log.info("Kafka 메시지 수신 - 종목코드: {}, 데이터: {}", stockCode, data);
            // 예시로 체결 가격을 매도호가 배열의 첫번째 값(최저 매도호가)로 가정
            String[] fields = data.split("\\^");
            if (fields.length < 42) {
                log.warn("필드 개수가 부족합니다: {}", data);
                return;
            }
            // 첫 번째 매도호가(fields[2])를 체결 가격으로 사용
            BigDecimal buyPrice = new BigDecimal(fields[2]);
            log.info("매수 체결용 체결 가격 (최저 매도호가): {}", buyPrice);
            // 첫 번째 매수호가(fields[12])를 체결 가격으로 사용
            BigDecimal sellPrice = new BigDecimal(fields[12]);
            log.info("매도 체결용 체결 가격 (첫 번째 매수호가): {}", sellPrice);

            // 체결 처리
            tradeService.processTrade(stockCode, buyPrice, sellPrice);
        } catch (Exception e) {
            log.error("Kafka 메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
