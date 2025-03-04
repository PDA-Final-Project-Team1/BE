package com.team1.etcore.trade.service;

import com.team1.etcore.trade.client.UserTradeHistoryClient;
import com.team1.etcore.trade.dto.TradeReq;
import com.team1.etcore.trade.dto.TradeRes;
import com.team1.etcore.trade.dto.TradeStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserTradeHistoryClient userTradeHistoryClient;
    private final RedisTemplate<String, Object> redisTemplate;
    /**
     * 사용자의 주문(거래) 생성 요청을 받아 ET-user 모듈에 주문 기록을 생성하고, Redis에 캐싱합니다.
     * @param tradeReq 주문 요청 정보
     * @return 생성된 주문(거래) 정보
     */
    public TradeRes createOrder(Long userId, TradeReq tradeReq) {
        try {
            TradeRes tradeRes = userTradeHistoryClient.createOrder(userId, tradeReq);
            String key = "trade:" + tradeRes.getId();
            redisTemplate.opsForValue().set(key, tradeRes); // TTL 없이 영구적으로 캐싱
            log.info("주문 생성 및 캐시 저장 완료: {}", tradeRes);
            return tradeRes;
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("주문 생성 실패", e);
        }
    }

    /**
     * Kafka에서 전달받은 체결 데이터를 기반으로 미체결 주문(PENDING)들을 조회하여 체결 조건에 맞으면 처리합니다.
     * 트랜잭션 내에서 주문 상태 업데이트, 예치금 처리, 보유 주식 업데이트를 진행하고,
     * 체결 완료 시 Redis 캐시에서 해당 주문 정보를 삭제합니다.
     *
     * @param stockCode  체결된 종목 코드
     * @param tradePrice 체결 가격
     */
    @Transactional
    public void processTrade(String stockCode, BigDecimal tradePrice) {
        try {
            List<TradeRes> pendingTrades = userTradeHistoryClient.getPendingOrders(stockCode, tradePrice);
            if (pendingTrades != null && !pendingTrades.isEmpty()) {
                for (TradeRes trade : pendingTrades) {
                    Long userId = trade.getUserId();
                    // 주문 상태 업데이트 (PENDING -> EXECUTED)
                    boolean statusUpdated = userTradeHistoryClient.updateOrderStatus(userId, trade.getId(), TradeStatus.EXECUTED);
                    if (!statusUpdated) {
                        log.warn("주문 내역 업데이트 실패: 주문 ID {}", trade.getId());
                        throw new RuntimeException("주문 내역 업데이트 실패. Trade ID: " + trade.getId());
                    }

                    // 주문 총액 계산: 주문 가격 * 주문 수량
                    BigDecimal totalAmount = trade.getPrice().multiply(BigDecimal.valueOf(trade.getAmount()));

                    // 주문 유형에 따라 예치금 처리: BUY인 경우 차감, SELL인 경우 충전
                    if (trade.getPosition().toString().equals("BUY")) {
                        boolean depositDeducted = userTradeHistoryClient.deductDeposit(userId, totalAmount);
                        if (!depositDeducted) {
                            log.warn("예치금 차감 실패: 주문 ID {}", trade.getId());
                            throw new RuntimeException("예치금 차감 실패. Trade ID: " + trade.getId());
                        }
                    } else if (trade.getPosition().toString().equals("SELL")) {
                        boolean depositCredited = userTradeHistoryClient.creditDeposit(userId, totalAmount);
                        if (!depositCredited) {
                            log.warn("예치금 충전 실패: 주문 ID {}", trade.getId());
                            throw new RuntimeException("예치금 충전 실패. Trade ID: " + trade.getId());
                        }
                    }

                    // 보유 주식 업데이트 (BUY: 추가 및 평균 단가 재계산, SELL: 차감)
                    boolean stockUpdated = userTradeHistoryClient.updateUserStock(
                            userId,
                            trade.getStockCode(),
                            trade.getAmount(),
                            trade.getPrice(),
                            trade.getPosition()
                    );
                    if (!stockUpdated) {
                        log.warn("보유 주식 업데이트 실패: 주문 ID {}", trade.getId());
                        throw new RuntimeException("보유 주식 업데이트 실패. Trade ID: " + trade.getId());
                    }
                    log.info("체결 성공: 주문 ID {} 체결 완료", trade.getId());

                    // 체결 완료되면 Redis 캐시 삭제
                    String key = "trade:" + trade.getId();
                    redisTemplate.delete(key);
                    log.info("캐시 삭제 완료: {}", key);
                }
            } else {
                log.info("종목코드 {}와 체결가 {}에 해당하는 미체결 주문이 없습니다.", stockCode, tradePrice);
            }
        } catch (Exception e) {
            log.error("체결 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("체결 처리 실패", e);
        }
    }
}
