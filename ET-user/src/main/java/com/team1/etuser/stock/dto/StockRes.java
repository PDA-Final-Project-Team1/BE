package com.team1.etuser.stock.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class StockRes {
    private String stockCode;
    private String name;
    private String market;
    private String img;
}
