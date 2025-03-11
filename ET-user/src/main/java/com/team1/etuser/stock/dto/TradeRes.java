package com.team1.etuser.stock.dto;

import com.team1.etuser.stock.domain.Position;
import com.team1.etuser.stock.domain.TradeStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TradeRes {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal amount;
    private Position position;            // "BUY" 또는 "SELL"
    private BigDecimal price;
    private String stockCode;
    private Long userId;
    private TradeStatus status;
}
