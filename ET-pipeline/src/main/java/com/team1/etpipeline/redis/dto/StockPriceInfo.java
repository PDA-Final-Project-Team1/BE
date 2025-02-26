package com.team1.etpipeline.redis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StockPriceInfo {
    private String localDate;
    private double closePrice;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private long accumulatedTradingVolume;
    private double foreignRetentionRate;
}