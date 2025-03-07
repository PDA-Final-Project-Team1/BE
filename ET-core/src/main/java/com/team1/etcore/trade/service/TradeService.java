package com.team1.etcore.trade.service;

import com.team1.etcore.trade.client.UserTradeHistoryClient;
import com.team1.etcore.trade.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserTradeHistoryClient userTradeHistoryClient;
    private final RedisTemplate<String, TradeRes> redisTemplate;
    private final TradeSettlement tradeSettlement;
    /**
     * 사용자의 주문(거래) 생성 요청을 받아 ET-user 모듈에 주문 기록을 생성하고, Redis에 캐싱합니다.
     * @param tradeReq 주문 요청 정보
     * @return 생성된 주문(거래) 정보
     */
    @Transactional
    public TradeRes createOrder(Long userId, TradeReq tradeReq) {
        try {
            BigDecimal totalPrice = tradeReq.getPrice().multiply(new BigDecimal(tradeReq.getAmount()));
            if (!userTradeHistoryClient.enoughDeposit(userId, totalPrice)) {
                throw new RuntimeException("예치금이 부족합니다.");
            }
            // 주문 생성
            TradeRes tradeRes = userTradeHistoryClient.createOrder(userId, tradeReq);

            String redisKey = buildRedisKey(tradeRes.getPosition(), tradeRes.getStockCode(), tradeRes.getPrice());

            double score = tradeRes.getCreatedAt().toEpochSecond(ZoneOffset.UTC);

            redisTemplate.opsForZSet().add(redisKey, tradeRes, score);

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
     * @param stockCode  체결된 종목 코드
     * @param tradePrice 체결 가격
     */
    @Transactional
    public void processMatching(Position position, String stockCode, BigDecimal tradePrice, int tradeAmount) {
        String redisKey = buildRedisKey(position, stockCode, tradePrice);
        Set<ZSetOperations.TypedTuple<TradeRes>> orders = redisTemplate.opsForZSet().rangeWithScores(redisKey, 0, -1);

        if (orders == null || orders.isEmpty()) {
            return;
        }

        for (ZSetOperations.TypedTuple<TradeRes> order : orders) {
            TradeRes tradeRes = order.getValue();

            Long userId = tradeRes.getUserId();
            Long historyId = tradeRes.getId();

            if (isPriceDifferent(tradePrice, tradeRes.getPrice())) continue;
            if (tradeRes.getAmount() > tradeAmount) continue;

            tradeSettlement.updateDeposit(position, userId, historyId,
                    tradeRes.getPrice(), tradeRes.getAmount());

            tradeSettlement.updateUserStock(userId, stockCode, tradeRes.getAmount(),
                    tradeRes.getPrice(), tradeRes.getPosition());

            tradeSettlement.updateHistoryStatus(historyId);

            log.info("체결 성공: 주문 ID {} 체결 완료", historyId);
            // 체결 완료되었으므로 Redis SortedSet에서 해당 주문 삭제
            redisTemplate.opsForZSet().remove(redisKey, tradeRes);
        }
    }

    // 체결가와 주문 가격이 같은지?
    private boolean isPriceDifferent(BigDecimal price1, BigDecimal price2) {
        return price1.compareTo(price2) != 0;
    }

    // 주문내역 Redis Key
    private String buildRedisKey(Position position, String stockCode, BigDecimal tradePrice) {
        String formattedPrice = tradePrice.stripTrailingZeros().toPlainString();
        return "orders:" + position.toString() + ":" + stockCode + ":" + formattedPrice;
    }
}
