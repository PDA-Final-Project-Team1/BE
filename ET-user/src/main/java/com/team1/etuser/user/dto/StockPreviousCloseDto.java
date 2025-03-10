package com.team1.etuser.user.dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

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
