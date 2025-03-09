package com.team1.etuser.user.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStocksRes {
    private String stockCode;
    private String stockImage;
    private String stockName;
    private BigDecimal amount;
    //보유종목
}
