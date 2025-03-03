package com.team1.etuser.user.dto;

import com.team1.etuser.user.domain.Position;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHistoryRes {
    private String stockCode;
    private String stockName;
    private BigDecimal price;
    private Position position;
    private int amount;

    @Override
    public String toString() {
        return "UserHistoryRes{" +
                "stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", price=" + price +
                ", position=" + position +
                ", amount=" + amount +
                '}';
    }
}
