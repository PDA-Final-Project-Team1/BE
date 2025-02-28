package com.team1.etuser.user.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationSseDTO {
    private int totalEvaluationAmount; // 유저 전체의 평가금액
    private BigDecimal totalRate;            // 전체 수익률
    private int stockEvaluationAmount; // 보유주식의 평가금액
    private BigDecimal stockRate;           // 보유주식의 수익율
}
