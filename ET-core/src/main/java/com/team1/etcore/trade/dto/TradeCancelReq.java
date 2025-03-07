package com.team1.etcore.trade.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeCancelReq {
    private Long tradeId;         // 거래 id
    private Position position;    // "BUY" 또는 "SELL"
    private String stockCode;   // 종목 코드
    private BigDecimal price;       // 주문 가격
}
