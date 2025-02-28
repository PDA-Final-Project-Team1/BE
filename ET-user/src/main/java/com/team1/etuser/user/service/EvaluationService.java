package com.team1.etuser.user.service;

import com.team1.etuser.user.dto.EvaluationSseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EvaluationService {

    public EvaluationSseDTO getEvaluationData() {
        int totalEval = calTotalEvaluationAmount();
        BigDecimal totalRate = calTotalRate();
        int stockEval = calStockEvaluationAmount();
        BigDecimal stockRate = calStockRate();

        return EvaluationSseDTO.builder()
                .totalEvaluationAmount(totalEval)
                .totalRate(totalRate)
                .stockEvaluationAmount(stockEval)
                .stockRate(stockRate)
                .build();
    }

    // 유저 전체의 평가금액 계산
    private int calTotalEvaluationAmount() {
        return 0;
    }

    // 전체 수익률 계산
    private BigDecimal calTotalRate() {
        return new BigDecimal(0);
    }

    // 보유주식의 평가금액 계산
    private int calStockEvaluationAmount() {
        return 0;
    }

    // 보유주식의 수익율 계산
    private BigDecimal calStockRate() {
        return new BigDecimal(0);
    }
}
