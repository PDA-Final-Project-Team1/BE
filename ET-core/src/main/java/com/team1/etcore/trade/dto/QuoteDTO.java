package com.team1.etcore.trade.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuoteDTO {
    private String stockCode;
    private BigDecimal buyPrice;
    private int buyAmount;
    private BigDecimal sellPrice;
    private int sellAmount;
}
