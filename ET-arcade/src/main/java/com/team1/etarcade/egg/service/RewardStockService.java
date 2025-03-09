package com.team1.etarcade.egg.service;

import com.team1.etarcade.egg.connector.StockFeignConnector;
import com.team1.etarcade.egg.connector.UserFeignConnector;
import com.team1.etarcade.egg.dto.StockPriceResponseDTO;
import com.team1.etarcade.egg.dto.UserFeignStockResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RewardStockService { // 소수점 주식 지급 서비스입니다.

    private final StockFeignConnector stockFeignConnector;
    private final UserFeignConnector userFeignConnector;

    //알 한번에 지급할 금액
    private static  final BigDecimal REWARD_MONEY = BigDecimal.valueOf(100.000);

    // 지급할 대표주 7개의 이름입니다.
    private static final List<String> PREFERRED_STOCKS = List.of(
            "삼성전자", "SK하이닉스", "신한투자증권", "현대", "기아", "네이버", "카카오"
    );

    public void giveRandomStockToUser(Long userId) {
        String randomStock = getRandomStock();
        StockPriceResponseDTO stockInfo = stockFeignConnector.getStockPrice(randomStock);
        BigDecimal stockPrice = stockInfo.getPrice();


        //RewordMoney에 맞게 줄 양 정하기.

        BigDecimal quantity = REWARD_MONEY.divide(stockPrice,4, RoundingMode.HALF_UP); //4번째자리에서 반올림.

        //fegin으로 post 요청
        UserFeignStockResponseDTO requestDTO = new UserFeignStockResponseDTO(userId, randomStock, quantity);
        userFeignConnector.addStockToUser(requestDTO);
    }

    private String getRandomStock() {
        Random random = new Random();
        return PREFERRED_STOCKS.get(random.nextInt(PREFERRED_STOCKS.size()));
    }
}