package com.team1.etcore.trade.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public String toString() {
        return "TradeRes{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", amount=" + amount +
                ", position=" + position +
                ", price=" + price +
                ", stockCode='" + stockCode + '\'' +
                ", userId=" + userId +
                ", status=" + status +
                '}';
    }
}
