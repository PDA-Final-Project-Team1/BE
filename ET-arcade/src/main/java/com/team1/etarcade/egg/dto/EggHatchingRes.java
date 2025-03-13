package com.team1.etarcade.egg.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EggHatchingRes {
    private String stockName;
    private BigDecimal amount;
    private String img;

}
