package com.team1.etcore.chart.dto;

import lombok.Data;

@Data
public class StockPriceInfo {
    private String localDate;
    private double closePrice;//종가
    private double openPrice;//시가
    private double highPrice;//고가
    private double lowPrice;//
    private long accumulatedTradingVolume;//거래량
    private double foreignRetentionRate;// 외국인지분
}