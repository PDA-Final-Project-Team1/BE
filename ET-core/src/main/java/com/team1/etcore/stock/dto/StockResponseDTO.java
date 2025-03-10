package com.team1.etcore.stock.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockResponseDTO implements Serializable {
    private String code;
    private String name;
    private String img;
}