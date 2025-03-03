package com.team1.etuser.user.dto;

import com.team1.etuser.user.domain.Position;
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
    private int amount;
    private LocalDateTime createdAt;
}
