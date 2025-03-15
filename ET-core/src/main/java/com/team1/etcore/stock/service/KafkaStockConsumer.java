package com.team1.etcore.stock.service;

import com.team1.etcore.stock.dto.AskStockPriceReq;
import com.team1.etcore.stock.dto.TradeStockPriceReq;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaStockConsumer {

    private final DataBuffer dataBuffer;
    private final SseService sseService;

    @KafkaListener(topics = "H0STCNT0", groupId = "stock-group")
    public void StockCurPriceData(ConsumerRecord<String, Object> record) {
        String stockCode = record.key(); // 종목 코드 (ex: "005930")
        Object stockData = record.value(); // 주식 현재가 데이터

        try {
            if (stockData instanceof String) {
                String data = (String) stockData;
                String[] splitData = data.split("\\^");

                if (splitData.length < 4) {
                    throw new IllegalArgumentException("잘못된 형식의 주식 데이터입니다: " + data);
                }

                String currentPrice = splitData[1];
                String priceChange = splitData[2];
                String changeRate = splitData[3];

                TradeStockPriceReq cur = new TradeStockPriceReq(stockCode, currentPrice, priceChange, changeRate);

                sseService.sendStockData(stockCode, "currentPrice", cur);
            } else {
                log.warn("주식 코드 {}: 문자열이 아닌 데이터가 수신되었습니다. 원본 데이터: {}", stockCode, stockData);
            }
        } catch (Exception e) {
            log.error("주식 코드 {}: StockCurPriceData 처리 중 오류 발생 - {}", stockCode, e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "H0STASP0", groupId = "stock-group")
    public void StockAskBidData(ConsumerRecord<String, Object> record) {
        String stockCode = record.key(); // 종목 코드 (ex: "005930")
        Object askBidData = record.value(); // 주식 호가 데이터

        try {
            if (askBidData instanceof String) {
                String data = (String) askBidData;
                String[] splitData = data.split("\\^");

                if (splitData.length < 37) {
                    throw new IllegalArgumentException("잘못된 형식의 호가 데이터입니다: " + data);
                }

                AskStockPriceReq askStockPriceReq = new AskStockPriceReq(
                        stockCode,
                        splitData[2], splitData[3], splitData[4], splitData[5], splitData[6],
                        splitData[12], splitData[13], splitData[14], splitData[15], splitData[16],
                        splitData[22], splitData[23], splitData[24], splitData[25], splitData[26],
                        splitData[32], splitData[33], splitData[34], splitData[35], splitData[36]
                );

                sseService.sendStockData(stockCode, "askBid", askStockPriceReq);
            } else {
                log.warn("주식 코드 {}: 문자열이 아닌 호가 데이터가 수신되었습니다. 원본 데이터: {}", stockCode, askBidData);
            }
        } catch (Exception e) {
            log.error("주식 코드 {}: StockAskBidData 처리 중 오류 발생 - {}", stockCode, e.getMessage(), e);
        }
    }
}