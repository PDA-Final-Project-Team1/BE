package com.team1.etuser.user.dto.feign;

import com.team1.etuser.user.domain.Position;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeReq {
    private BigDecimal amount;         // 주식 개수
    private Position position;    // "BUY" 또는 "SELL"
    private BigDecimal price;       // 주문 가격
    private String stockCode;   // 종목 코드
//    private Long userId;      // 사용자 ID
}
