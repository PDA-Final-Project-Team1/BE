package com.team1.etarcade.egg.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockNameAndCodeDTO {
    private String code;   // 주식 코드 (예: "005930")
    private String name;   // 주식 이름 (예: "삼성전자")
}
