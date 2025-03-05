package com.team1.etcore.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockResponseDTO {
    private String code;
    private String name;
    private String img;
}