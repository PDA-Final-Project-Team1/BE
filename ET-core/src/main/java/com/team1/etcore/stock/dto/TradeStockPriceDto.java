package com.team1.etcore.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TradeStockPriceDto {

    private String stockCode;   // 종목코드
    private String currentPrice; // 주식 현재가
    private String priceChange;  // 전일 대비 변동 금액
    private String changeRate; // 전일 대비 변동률
}
