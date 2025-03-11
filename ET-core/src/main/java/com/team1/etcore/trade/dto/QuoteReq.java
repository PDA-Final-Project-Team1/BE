package com.team1.etcore.trade.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuoteReq {
    private String stockCode;
    private BigDecimal buyPrice;
    private BigDecimal buyAmount;
    private BigDecimal sellPrice;
    private BigDecimal sellAmount;
}
