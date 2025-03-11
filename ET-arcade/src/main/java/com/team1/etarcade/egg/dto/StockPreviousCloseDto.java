package com.team1.etarcade.egg.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPreviousCloseDto {
    private String stockCode;
    @Setter
    private BigDecimal closingPrice;

    public StockPreviousCloseDto(String stockCode, int previousClose) {
        this.stockCode = stockCode;
        this.closingPrice = BigDecimal.valueOf(previousClose);
    }
}
