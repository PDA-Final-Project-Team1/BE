package com.team1.etuser.user.dto;

import com.team1.etuser.user.domain.Position;
import com.team1.etuser.user.domain.TradeStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHistoryRes {
    private String stockCode;
    private String stockName;
    private BigDecimal price;
    private Position position;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TradeStatus tradeStatus;
}
