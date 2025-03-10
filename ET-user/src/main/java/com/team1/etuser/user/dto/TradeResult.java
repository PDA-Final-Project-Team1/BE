package com.team1.etuser.user.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeResult {
    private Long userId;
    @Setter
    private String message;
    private String stockCode;
    private BigDecimal stockAmount;
    private BigDecimal stockPrice;
}
