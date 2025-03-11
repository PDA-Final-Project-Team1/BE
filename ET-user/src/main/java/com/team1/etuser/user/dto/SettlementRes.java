package com.team1.etuser.user.dto;

import com.team1.etuser.stock.domain.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRes {
    private Long userId;
    private Long historyId;
    private String stockCode;
    private Position position;
    private BigDecimal orderPrice;
    private BigDecimal orderAmount;
    private Position orderPosition;

    @Override
    public String toString() {
        return "SettlementDTO{" +
                "userId=" + userId +
                ", historyId=" + historyId +
                ", stockCode='" + stockCode + '\'' +
                ", position=" + position +
                ", orderPrice=" + orderPrice +
                ", orderAmount=" + orderAmount +
                ", orderPosition=" + orderPosition +
                '}';
    }
}
