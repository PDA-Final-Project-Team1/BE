package com.team1.etcore.stock.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeResultRes {
    private Long userId;
    private String message;
    private String stockCode;
    private BigDecimal stockAmount;
    private BigDecimal stockPrice;
    private String name;
    private String img;
}

