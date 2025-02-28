package com.team1.etarcade.egg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
//주식과 가격을 전달하는 DTO입니다.
public class StockPriceResponseDTO {

    private String stockname;
    private Double price;
}
