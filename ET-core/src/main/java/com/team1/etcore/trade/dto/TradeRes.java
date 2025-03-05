package com.team1.etcore.trade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeRes {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int amount;
    private Position position;            // "BUY" 또는 "SELL"
    private BigDecimal price;
    private String stockCode;
    private Long userId;
    private TradeStatus status;
}
