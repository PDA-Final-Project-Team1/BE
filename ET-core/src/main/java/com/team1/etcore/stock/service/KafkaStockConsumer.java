package com.team1.etcore.stock.service;

import com.team1.etcore.stock.dto.AskStockPriceDto;
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
    public void StockCurPriceData(ConsumerRecord<String, Object> record) {
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
            sseService.sendToClientsStockCurPrice(stockCode,cur);

        } else {
            System.out.println("Received data is not of type String.");
        }

    }

    @KafkaListener(topics = "H0STASP0", groupId = "stock-group")
    public void StockAskBidData(ConsumerRecord<String, Object> record) {
        String stockCode = record.key(); // 종목 코드 (ex: "005930")
        Object askBidData = record.value(); // 주식 호가 데이터
        if (askBidData instanceof String) {
            String data = (String) askBidData;  // Object를 String으로 캐스팅

            String[] splitData = data.split("\\^");

            String askp1 = splitData[2];
            String askp2 = splitData[3];
            String askp3 = splitData[4];
            String askp4 = splitData[5];
            String askp5 = splitData[6];

            String bidp1 = splitData[12];
            String bidp2 = splitData[13];
            String bidp3 = splitData[14];
            String bidp4 = splitData[15];
            String bidp5 = splitData[16];

            String askRSQN1 = splitData[22];
            String askRSQN2 = splitData[23];
            String askRSQN3 = splitData[24];
            String askRSQN4 = splitData[25];
            String askRSQN5 = splitData[26];

            String bidRSQN1 = splitData[32];
            String bidRSQN2 = splitData[33];
            String bidRSQN3 = splitData[34];
            String bidRSQN4 = splitData[35];
            String bidRSQN5 = splitData[36];

            AskStockPriceDto askStockPriceDto = new AskStockPriceDto(
                    stockCode,  // 종목코드
                    splitData[2], splitData[3], splitData[4], splitData[5], splitData[6],  // 매도호가
                    splitData[12], splitData[13], splitData[14], splitData[15], splitData[16],  // 매수호가
                    splitData[22], splitData[23], splitData[24], splitData[25], splitData[26],  // 매도잔량
                    splitData[32], splitData[33], splitData[34], splitData[35], splitData[36]   // 매수잔량
            );
            sseService.sendToClientsAskBidStockPrice(stockCode,askStockPriceDto);

        } else {
            System.out.println("Received data is not of type String.");
        }

    }

}
