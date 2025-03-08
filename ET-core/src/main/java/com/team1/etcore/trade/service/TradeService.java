package com.team1.etcore.trade.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etcore.trade.client.UserTradeHistoryClient;
import com.team1.etcore.trade.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserTradeHistoryClient userTradeHistoryClient;
    private final RedisTemplate<String, TradeRes> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

    // 거래 생성 및 Redis 저장
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
        String formattedPrice = price.stripTrailingZeros().toPlainString();

        String key = "orders:" + position.name() + ":" + stockCode + ":" + formattedPrice;
        Set<TradeRes> tradeSet = redisTemplate.opsForZSet().range(key, 0, -1);
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
        if (obj instanceof TradeRes) {
            return ((TradeRes) obj).getId();
        }
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



    // Kafka에서 전달받은 호가 데이터를 기반으로 미체결 주문들을 조회하여 체결 조건에 맞으면 처리합니다.
    @Transactional
    public void processMatching(Position position, String stockCode, BigDecimal tradePrice, int tradeAmount) {
        String redisKey = buildRedisKey(position, stockCode, tradePrice);
        Set<ZSetOperations.TypedTuple<TradeRes>> orders = redisTemplate.opsForZSet().rangeWithScores(redisKey, 0, -1);

        if (orders == null || orders.isEmpty()) {
            return;
        }

        for (ZSetOperations.TypedTuple<TradeRes> order : orders) {
            TradeRes tradeRes = order.getValue();

            Long userId = Objects.requireNonNull(tradeRes).getUserId();
            Long historyId = tradeRes.getId();

            if (isPriceDifferent(tradePrice, tradeRes.getPrice())) continue;
            if (tradeRes.getAmount() > tradeAmount) continue;

            SettlementDTO settlementDTO = SettlementDTO.builder()
                    .userId(userId)
                    .historyId(historyId)
                    .stockCode(stockCode)
                    .position(position)
                    .orderPrice(tradeRes.getPrice())
                    .orderAmount(tradeRes.getAmount())
                    .orderPosition(tradeRes.getPosition())
                    .build();

            redisTemplate.opsForZSet().remove(redisKey, tradeRes);

            try {
                kafkaTemplate.send("settlement", objectMapper.writeValueAsString(settlementDTO));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON 파싱에 실패했습니다.");
            }

            log.info("체결 성공: 주문 ID {} 체결 완료", historyId);
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
