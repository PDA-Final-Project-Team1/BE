package com.team1.etuser.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStocksRes {
    private String stockCode;
    private String stockImage;
    private String stockName;
    private int amount;
}
