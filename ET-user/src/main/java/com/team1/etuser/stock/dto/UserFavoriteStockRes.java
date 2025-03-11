package com.team1.etuser.stock.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFavoriteStockRes {
    private String stockCode;
    private String stockName;
}
