package com.team1.etcore.chart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class StockRes {
    private String stockCode;
    private String infoType;
    private String periodType;
    private boolean newlyListed;
    private String stockExchangeType;
    private boolean hasVolume;
    private int decimalUnit;

    @JsonProperty("priceInfos")
    private List<StockPriceInfoRes> priceInfos;
}