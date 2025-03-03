package com.team1.etuser.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class StockDTO {
    private String stockCode;
    private String name;
    private String market;
    private String img;
}
