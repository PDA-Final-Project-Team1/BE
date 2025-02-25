package com.team1.etpipeline.redis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class StockResponseDto {
    private String stockId;
    private String infoType;
    private String periodType;
    private boolean newlyListed;
    private String stockExchangeType;
    private boolean hasVolume;
    private int decimalUnit;

    @JsonProperty("priceInfos")
    private List<StockPriceInfo> priceInfos;
}