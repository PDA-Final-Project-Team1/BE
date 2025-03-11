package com.team1.etcore.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AskStockPriceReq {
    private String stockCode;   // 종목코드
    private String askp1;
    private String askp2;
    private String askp3;
    private String askp4;
    private String askp5;

    private String bidp1;
    private String bidp2;
    private String bidp3;
    private String bidp4;
    private String bidp5;

    private String askRSQN1;
    private String askRSQN2;
    private String askRSQN3;
    private String askRSQN4;
    private String askRSQN5;

    private String bidRSQN1;
    private String bidRSQN2;
    private String bidRSQN3;
    private String bidRSQN4;
    private String bidRSQN5;

}
