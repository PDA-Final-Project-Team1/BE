package com.team1.etcore.trade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etcore.trade.client.UserTradeHistoryClient;
import com.team1.etcore.trade.dto.Position;
import com.team1.etcore.trade.dto.TradeHistoryRedisRes;
import com.team1.etcore.trade.dto.TradeReq;
import com.team1.etcore.trade.dto.TradeRes;
import com.team1.etcore.trade.dto.TradeStatus;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 사용자의 주문(거래) 생성 요청을 받아 ET-user 모듈에 주문 기록을 생성하고, Redis에 캐싱합니다.
     * @param tradeReq 주문 요청 정보
     * @return 생성된 주문(거래) 정보
     */
    public TradeRes createOrder(Long userId, TradeReq tradeReq) {
        try {
            if (!userTradeHistoryClient.enoughDeposit(userId, tradeReq.getPrice().multiply(new BigDecimal(tradeReq.getAmount())))) {
                throw new RuntimeException("예치금이 부족합니다.");
            }
            // 주문 생성
            TradeRes tradeRes = userTradeHistoryClient.createOrder(userId, tradeReq);

            String side = tradeRes.getPosition().toString(); // "BUY" 또는 "SELL"
            String key = "orders:" + side + ":" + tradeRes.getStockCode() + ":" + tradeRes.getPrice();

            double score = tradeRes.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
            redisTemplate.opsForZSet().add(key, tradeRes, score);

            log.info("주문 생성 및 캐시 저장 완료: {}", tradeRes);
            return tradeRes;
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("주문 생성 실패", e);
        }
    }

    /**
     * 거래 취소:
     * 1. MySQL DB에 저장된 거래 내역(UserTradeHistory)의 status를 CANCELLED로 업데이트
     * 2. Redis에서 key(orders:{position}:{stockCode}:{price})로 조회한 후,
     *    반환된 값들 중 tradeId와 일치하는 항목을 삭제
     * 위 2가지 작업이 모두 성공되어야 트랜잭션이 커밋됩니다.
     *
     * @param tradeId   거래 ID
     * @param position  주문 포지션 (BUY/SELL)
     * @param stockCode 주식 코드
     * @param price     주식 가격
     */

    @Transactional
    public void cancelOrder(Long tradeId, Position position, String stockCode, BigDecimal price) {
        // 1. DB 업데이트: UserTradeHistory의 상태를 CANCELLED로 변경
        boolean updateResult = userTradeHistoryClient.updateHistoryStatus(tradeId, TradeStatus.CANCELLED);
        if (!updateResult) {
            throw new RuntimeException("DB 거래 상태 업데이트에 실패했습니다. tradeId=" + tradeId);
        }
        log.info("DB 거래 상태 업데이트 완료: tradeId={}", tradeId);

        // 2. Redis에서 해당 값 삭제: 해당 주문을 캐싱한 ZSet에서 tradeId에 해당하는 항목을 제거
        String key = "orders:" + position.name() + ":" + stockCode + ":" + price;
        Set<Object> tradeSet = redisTemplate.opsForZSet().range(key, 0, -1);
        if (tradeSet == null || tradeSet.isEmpty()) {
            throw new RuntimeException("Redis key [" + key + "]에 데이터가 없습니다. tradeId=" + tradeId);
        }

        boolean removed = false;
        for (Object obj : tradeSet) {
            Long parsedTradeId = parseTradeId(obj);
            if (parsedTradeId != null && parsedTradeId.equals(tradeId)) {
                Long removedCount = redisTemplate.opsForZSet().remove(key, obj);
                if (removedCount == null || removedCount == 0) {
                    throw new RuntimeException("Redis에서 tradeId 제거에 실패했습니다. tradeId=" + tradeId);
                }
                removed = true;
                log.info("Redis에서 tradeId 제거 완료: tradeId={}", tradeId);
                break;
            }
        }
        if (!removed) {
            throw new RuntimeException("Redis에서 tradeId를 찾지 못했습니다. tradeId=" + tradeId);
        }
    }
    /**
     * Redis에 저장된 데이터에서 tradeId를 추출합니다.
     * 데이터는 두 가지 형태로 저장될 수 있습니다.
     * 1) JSON 형태: "{\"id\":2, ...}"
     * 2) 숫자 문자열: "1741252398"
     *
     * @param obj Redis에서 반환된 데이터
     * @return 추출된 tradeId, 실패 시 null
     */
    private Long parseTradeId(Object obj) {
        if (obj instanceof String) {
            String data = (String) obj;
            try {
                // JSON 형태의 문자열인 경우
                if (data.startsWith("{")) {
                    TradeHistoryRedisRes tradeHistoryRedisRes = objectMapper.readValue(data, TradeHistoryRedisRes.class);
                    return tradeHistoryRedisRes.getTradeId();
                }
                // 숫자 문자열인 경우
                else if (data.matches("\\d+")) {
                    return Long.valueOf(data);
                }
            } catch (Exception e) {
                log.error("Redis 데이터 파싱 중 오류 발생: {}", e.getMessage(), e);
            }
        }
        return null;
    }

    @Transactional
    public void processTrade(String stockCode, BigDecimal buyPrice, BigDecimal sellPrice) {
        try {
            // 매수 주문 처리: "orders:BUY:{stockCode}:{tradePrice}"
            processMatching(Position.BUY, stockCode, buyPrice);
            // 매도 주문 처리: "orders:SELL:{stockCode}:{tradePrice}"
            processMatching(Position.SELL, stockCode, sellPrice);
        } catch (Exception e) {
            log.error("체결 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("체결 처리 실패", e);
        }
    }


    /**
     * Kafka에서 전달받은 체결 데이터를 기반으로 미체결 주문(PENDING)들을 조회하여 체결 조건에 맞으면 처리합니다.
     * 트랜잭션 내에서 주문 상태 업데이트, 예치금 처리, 보유 주식 업데이트를 진행하고,
     * 체결 완료 시 Redis 캐시에서 해당 주문 정보를 삭제합니다.
     * @param stockCode  체결된 종목 코드
     * @param tradePrice 체결 가격
     */
    public void processMatching(Position side, String stockCode, BigDecimal tradePrice) {
        String sortedSetKey = "orders:" + side.toString() + ":" + stockCode + ":" + tradePrice;
        Set<ZSetOperations.TypedTuple<Object>> matchingTrades =
                redisTemplate.opsForZSet().rangeWithScores(sortedSetKey, 0, -1);

        if (matchingTrades != null && !matchingTrades.isEmpty()) {
            for (ZSetOperations.TypedTuple<Object> tuple : matchingTrades) {
                TradeRes tradeRes = (TradeRes) tuple.getValue();
                if (tradeRes == null) continue;

                Long historyId = tradeRes.getId();
                Long userId = tradeRes.getUserId();

                if (tradeRes.getPrice().compareTo(tradePrice) == 0) {
                    // 주문 총액 = 가격 * 개수
                    BigDecimal totalPrice = tradeRes.getPrice().multiply(BigDecimal.valueOf(tradeRes.getAmount()));

                    // 주문 유형(BUY, SELL)에 따른 예치금 처리
                    if (side.equals(Position.BUY)) {
                        boolean depositDeducted = userTradeHistoryClient.updateDeposit(userId, totalPrice.negate());
                        if (!depositDeducted) {
                            log.warn("예치금 차감에 실패했습니다: 주문 ID {}", historyId);
                            throw new RuntimeException("예치금 차감 실패. 주문 ID: " + historyId);
                        }
                    } else if (side.equals(Position.SELL)) {
                        boolean depositCredited = userTradeHistoryClient.updateDeposit(userId, totalPrice);
                        if (!depositCredited) {
                            log.warn("예치금 충전에 실패했습니다: 주문 ID {}", historyId);
                            throw new RuntimeException("예치금 충전 실패. 주문 ID: " + historyId);
                        }
                    }

                    // 보유 주식 업데이트
                    boolean stockUpdated = userTradeHistoryClient.updateUserStock(
                            userId,
                            stockCode,
                            tradeRes.getAmount(),
                            tradeRes.getPrice(),
                            tradeRes.getPosition());

                    if (!stockUpdated) {
                        log.warn("보유 주식 업데이트에 실패했습니다: 주문 ID {}", historyId);
                        throw new RuntimeException("보유 주식 업데이트 실패. 주문 ID: " + historyId);
                    }

                    // 주문 상태를 업데이트 (PENDING -> EXECUTED)
                    boolean statusUpdated = userTradeHistoryClient.updateHistoryStatus(historyId, TradeStatus.EXECUTED);
                    if (!statusUpdated) {
                        log.warn("최종 주문 상태 업데이트에 실패했습니다: 주문 ID {}", historyId);
                        throw new RuntimeException("최종 주문 상태 업데이트 실패. 주문 ID: " + historyId);
                    }

                    log.info("체결 성공: 주문 ID {} 체결 완료", historyId);
                    // 체결 완료되었으므로 Redis SortedSet에서 해당 주문 삭제
                    redisTemplate.opsForZSet().remove(sortedSetKey, tradeRes);
                }
            }
        } else {
            log.info("키 {}에 해당하는 미체결 주문이 없습니다.", sortedSetKey);
        }
    }
}
