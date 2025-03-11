package com.team1.etuser.stock.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockClosePriceRes {
    private String stockCode;
    @Setter
    private BigDecimal closingPrice;

    public StockClosePriceRes(String stockCode, int previousClose) {
        this.stockCode = stockCode;
        this.closingPrice = BigDecimal.valueOf(previousClose);
    }
}
