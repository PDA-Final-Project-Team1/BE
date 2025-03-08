package com.team1.etarcade.egg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class PointUpdateReq {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class PointUpdateRequest {
        private Long userId;
        private BigDecimal deposit;
        private int newPoint;
        private String account;

    }
}
