package com.team1.etcore.trade.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFavoriteStocksRes {
    private String stockCode;
    private String stockName;
}
