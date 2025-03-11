package com.team1.etuser.stock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etuser.stock.domain.Position;
import com.team1.etuser.stock.domain.TradeStatus;
import com.team1.etuser.user.dto.SettlementRes;
import com.team1.etuser.stock.dto.TradeResultReq;
import com.team1.etuser.user.service.UserAdditionalService;
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
    private final TradeHistoryService tradeHistoryService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void processSettlement(SettlementRes dto) {
        boolean isSuccessDeposit = settlementDeposit(dto.getPosition(), dto.getUserId(), dto.getHistoryId(),
                dto.getOrderPrice(), dto.getOrderAmount());

        boolean isSuccessUserStock = settlementUserStock(dto.getUserId(), dto.getStockCode(), dto.getOrderAmount(),
                dto.getOrderPrice(), dto.getOrderPosition());

        boolean isSuccessStatusUpdate = settlementHistoryStatus(dto.getHistoryId());

        TradeResultReq tradeResultReq = TradeResultReq.builder()
                .userId(dto.getUserId())
                .stockCode(dto.getStockCode())
                .stockPrice(dto.getOrderPrice())
                .stockAmount(dto.getOrderAmount())
                .build();

        if (isSuccessDeposit && isSuccessUserStock && isSuccessStatusUpdate) {
            tradeResultReq.setMessage("Success");
            resultSend(tradeResultReq);
            log.info(">>> 정산 완료. userId : {}, historyId : {}", dto.getUserId(), dto.getHistoryId());
            return;
        }

        tradeResultReq.setMessage("Error");
        resultSend(tradeResultReq);
        log.warn("정산 과정에서 오류가 발생했습니다. userId : {}, historyId : {}", dto.getUserId(), dto.getHistoryId());
    }

    // 예치금 정산
    private boolean settlementDeposit(Position position, Long userId, Long historyId, BigDecimal price, BigDecimal amount) {
        // 주문 총액 = 가격 * 개수
        BigDecimal totalPrice = price.multiply(amount);

        if (position.equals(Position.BUY)) {
            boolean depositDeducted = userAdditionalService.updateDeposit(userId, totalPrice.negate());
            if (!depositDeducted) {
                log.warn("예치금 차감에 실패했습니다: 주문 ID {}", historyId);
                return false;
            }
        } else if (position.equals(Position.SELL)) {
            boolean depositCredited = userAdditionalService.updateDeposit(userId, totalPrice);
            if (!depositCredited) {
                log.warn("예치금 충전에 실패했습니다: 주문 ID {}", historyId);
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    // 보유주식 정산
    private boolean settlementUserStock(Long userId, String stockCode, BigDecimal amount, BigDecimal price, Position position) {
        boolean isStockUpdated = userStockService.updateUserStock(
                userId,
                stockCode,
                amount,
                price,
                position);

        if (!isStockUpdated) {
            log.warn("보유 주식 업데이트에 실패했습니다: 유저 ID {}", userId);
            return false;
        }

        return true;
    }

    // 거래내역 정산
    private boolean settlementHistoryStatus(Long historyId) {
        boolean isStatusUpdated = tradeHistoryService.updateHistoryStatus(historyId, TradeStatus.EXECUTED);
        if (!isStatusUpdated) {
            log.warn("최종 주문 상태 업데이트에 실패했습니다: 주문 ID {}", historyId);
            return false;
        }

        return true;
    }

    private void resultSend(TradeResultReq tradeResultReq) {
        try {
            kafkaTemplate.send("tradeResult", objectMapper.writeValueAsString(tradeResultReq));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱에 실패했습니다.");
        }
    }
}
