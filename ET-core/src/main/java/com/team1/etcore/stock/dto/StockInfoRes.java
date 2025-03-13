package com.team1.etcore.stock.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockInfoRes {
    private String code;   // 주식 코드 (예: "005930")
    private String name;   // 주식 이름 (예: "삼성전자")
    private String img;
}
