package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.Position;
import com.team1.etuser.user.domain.TradeStatus;
import com.team1.etuser.user.dto.SettlementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final UserAdditionalService userAdditionalService;
    private final UserStockService userStockService;
    private final UserTradeHistoryService userTradeHistoryService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void processSettlement(SettlementDTO dto) {
        settlementDeposit(dto.getPosition(), dto.getUserId(), dto.getHistoryId(),
                dto.getOrderPrice(), dto.getOrderAmount());

        settlementUserStock(dto.getUserId(), dto.getStockCode(), dto.getOrderAmount(),
                dto.getOrderPrice(), dto.getOrderPosition());

        settlementHistoryStatus(dto.getHistoryId());

        log.info(">>> 정산 완료");
    }

    // 예치금 정산
    private void settlementDeposit(Position position, Long userId, Long historyId, BigDecimal price, BigDecimal amount) {
        // 주문 총액 = 가격 * 개수
        BigDecimal totalPrice = price.multiply(amount);

        if (position.equals(Position.BUY)) {
            boolean depositDeducted = userAdditionalService.updateDeposit(userId, totalPrice.negate());
            if (!depositDeducted) {
                log.warn("예치금 차감에 실패했습니다: 주문 ID {}", historyId);
                errorSend();
                throw new RuntimeException("예치금 차감 실패. 주문 ID: " + historyId);
            }
        } else if (position.equals(Position.SELL)) {
            boolean depositCredited = userAdditionalService.updateDeposit(userId, totalPrice);
            if (!depositCredited) {
                log.warn("예치금 충전에 실패했습니다: 주문 ID {}", historyId);
                errorSend();
                throw new RuntimeException("예치금 충전 실패. 주문 ID: " + historyId);
            }
        } else {
            throw new RuntimeException("잘못된 주문 유형입니다.");
        }
    }

    // 보유주식 정산
    private void settlementUserStock(Long userId, String stockCode, BigDecimal amount, BigDecimal price, Position position) {
        boolean isStockUpdated = userStockService.updateUserStock(
                userId,
                stockCode,
                amount,
                price,
                position);

        if (!isStockUpdated) {
            log.warn("보유 주식 업데이트에 실패했습니다: 유저 ID {}", userId);
            errorSend();
            throw new RuntimeException("보유 주식 업데이트 실패. 유저 ID: " + userId);
        }
    }

    // 거래내역 정산
    private void settlementHistoryStatus(Long historyId) {
        boolean isStatusUpdated = userTradeHistoryService.updateHistoryStatus(historyId, TradeStatus.EXECUTED);
        if (!isStatusUpdated) {
            log.warn("최종 주문 상태 업데이트에 실패했습니다: 주문 ID {}", historyId);
            errorSend();
            throw new RuntimeException("최종 주문 상태 업데이트 실패. 주문 ID: " + historyId);
        }
    }

    private void errorSend() {
        kafkaTemplate.send("tradeError", "Error");
    }
}
