package com.team1.etuser.user.dto.feign;

import com.team1.etuser.user.domain.Position;
import com.team1.etuser.user.domain.TradeStatus;
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
    private int amount;
    private Position position;            // "BUY" 또는 "SELL"
    private BigDecimal price;
    private String stockCode;
    private Long userId;
    private TradeStatus status;
}
