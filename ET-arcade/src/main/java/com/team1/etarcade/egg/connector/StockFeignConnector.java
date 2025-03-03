package com.team1.etarcade.egg.connector;


import com.team1.etarcade.egg.dto.StockPriceResponseDTO;
import com.team1.etarcade.egg.dto.UserFeignStockResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Map;


//Fegin은 stock 값이 들어오면 작성

//@FeignClient(name = "ET-core", path = "/api/stocks") // Eureka를 통한 탐색
//public interface StockClient {
//
//    @GetMapping("/{stockName}/price")
//    StockPriceResponseDTO getStockPrice(@PathVariable("stockName") String stockName);
//}


@Component
public class StockFeignConnector {
    //stock에서 선호 대형주 (삼전, sk하닉 , 신한투자증권,  현대, 기아, 네이버, 카카오,) 중에 하나 랜덤으로 뽑음
    //뽑은 주식을 N원이 되게 현재 단가 계산 . 해당 양 만큼 유저 보유주식에 POST 해줌 .


    //지금은 자료형으로 주식이름, 현재가를 임시로 채워넣음.


    private static final Map<String, Double> STOCK_PRICES = Map.of(
            "삼성전자", 75000.0,
            "SK하이닉스", 120000.0,
            "신한투자증권", 45000.0,
            "현대", 90000.0,
            "기아", 80000.0,
            "네이버", 350000.0,
            "카카오", 65000.0);


    public StockPriceResponseDTO getStockPrice(String randomStock) {
        double price = STOCK_PRICES.getOrDefault(randomStock, 50000.0); // 기본값 50000
        return new StockPriceResponseDTO(randomStock, price);

    }
}