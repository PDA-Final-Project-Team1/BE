package com.team1.etcore.stock.service;

import com.team1.etcore.stock.dto.TradeStockPriceDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaStockConsumer {

    private final SseService sseService;

    public KafkaStockConsumer(SseService sseService) {
        this.sseService = sseService;
    }

    @KafkaListener(topics = "H0STCNT0", groupId = "stock-group")
    public void consumeStockData(ConsumerRecord<String, Object> record) {
        String stockCode = record.key(); // 종목 코드 (ex: "005930")
        Object stockData = record.value(); // 주식 현재가 데이터
        if (stockData instanceof String) {
            String data = (String) stockData;  // Object를 String으로 캐스팅
            String[] splitData = data.split("\\^");  // ^로 구분하여 분리
            String currentPrice = splitData[1];  // 주식 현재가
            String priceChange = splitData[2];  // 전일 대비 변동 금액
            String changeRate = splitData[3];  // 전일 대비 변동률
            TradeStockPriceDto cur = new TradeStockPriceDto(stockCode, currentPrice, priceChange, changeRate);

            sseService.sendToClientsInterestStockPrice(stockCode,cur);
            sseService.sendToClientsPortfolioStockPrice(stockCode,cur);

        } else {
            System.out.println("Received data is not of type String.");
        }

    }

}
