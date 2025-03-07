package com.team1.etcore.trade.service;

import com.team1.etcore.trade.client.UserTradeHistoryClient;
import com.team1.etcore.trade.dto.Position;
import com.team1.etcore.trade.dto.TradeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component
public class TradeSettlement { // 정산
    private final UserTradeHistoryClient userTradeHistoryClient;

    public void updateDeposit(Position position, Long userId, Long historyId, BigDecimal price, int amount) {
        // 주문 총액 = 가격 * 개수
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(amount));

        if (position.equals(Position.BUY)) {
            boolean depositDeducted = userTradeHistoryClient.updateDeposit(userId, totalPrice.negate());
            if (!depositDeducted) {
                log.warn("예치금 차감에 실패했습니다: 주문 ID {}", historyId);
                throw new RuntimeException("예치금 차감 실패. 주문 ID: " + historyId);
            }
        } else if (position.equals(Position.SELL)) {
            boolean depositCredited = userTradeHistoryClient.updateDeposit(userId, totalPrice);
            if (!depositCredited) {
                log.warn("예치금 충전에 실패했습니다: 주문 ID {}", historyId);
                throw new RuntimeException("예치금 충전 실패. 주문 ID: " + historyId);
            }
        } else {
            throw new RuntimeException("잘못된 주문 유형입니다.");
        }
    }

    // 사용자 보유 주식 업데이트
    public void updateUserStock(Long userId, String stockCode, int amount, BigDecimal price, Position position) {
        // 보유 주식 업데이트
        boolean isStockUpdated = userTradeHistoryClient.updateUserStock(
                userId,
                stockCode,
                amount,
                price,
                position);

        throw new RuntimeException(">>>>>>>>>롤 백 테스트");

//        if (!isStockUpdated) {
//            log.warn("보유 주식 업데이트에 실패했습니다: 유저 ID {}", userId);
//            throw new RuntimeException("보유 주식 업데이트 실패. 유저 ID: " + userId);
//        }
    }

    // 거래내역 업데이트
    public void updateHistoryStatus(Long historyId) {
        boolean isStatusUpdated = userTradeHistoryClient.updateHistoryStatus(historyId, TradeStatus.EXECUTED);
        if (!isStatusUpdated) {
            log.warn("최종 주문 상태 업데이트에 실패했습니다: 주문 ID {}", historyId);
            throw new RuntimeException("최종 주문 상태 업데이트 실패. 주문 ID: " + historyId);
        }
    }
}
