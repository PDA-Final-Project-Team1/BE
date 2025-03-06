package com.team1.etcore.trade.service;

import com.team1.etcore.trade.dto.QuoteDTO;
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
//            log.info("Kafka 메시지 수신 - 종목코드: {}, 데이터: {}", stockCode, value);

            String[] quoteData = value.split("\\^");
            if (quoteData.length < 42) {
                log.warn("필드 개수가 부족합니다: {}", value);
                return;
            }

            QuoteDTO quoteDTO = QuoteDTO.builder()
                    .stockCode(stockCode)
                    .buyPrice(new BigDecimal(quoteData[2]))
                    .buyAmount(Integer.parseInt(quoteData[22]))
                    .sellPrice(new BigDecimal(quoteData[12]))
                    .sellAmount(Integer.parseInt(quoteData[32]))
                    .build();
            // 체결 처리
            tradeService.processTrade(quoteDTO);
        } catch (Exception e) {
            log.error("Kafka 메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
