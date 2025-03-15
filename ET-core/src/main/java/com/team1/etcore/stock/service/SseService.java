package com.team1.etcore.stock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etcore.stock.domain.Stock;
import com.team1.etcore.stock.dto.ConnectRes;
import com.team1.etcore.stock.dto.TradeResultRes;
import com.team1.etcore.stock.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> stockSubscribers = new ConcurrentHashMap<>();
    private final Map<Long, SseEmitter> tradeSubscribers = new ConcurrentHashMap<>();

    private final StockRepository stockRepository;

    public SseService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // 유저별로 단 하나의 SSE를 생성/관리
//    public SseEmitter subscribeUser(String userId, List<String> stockCodes) {
//        // 기존 Emitter 제거
//        SseEmitter oldEmitter = userEmitters.get(userId);
//        if (oldEmitter != null) {
//            oldEmitter.complete();
//            userEmitters.remove(userId);
//        }
//
//        // 새 Emitter 생성
//        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
//        emitter.onCompletion(() -> userEmitters.remove(userId));
//        emitter.onTimeout(() -> userEmitters.remove(userId));
//
//        userEmitters.put(userId, emitter);
//
//        // 관심 종목 + 보유 종목을 합쳐서 한번에 처리
//        // (원한다면 별도 메서드로 분리 가능)
//        removeUserFromAllStocks(userId);
//
//        // 새로 전달받은 stockCodes를 구독
//        if (stockCodes != null) {
//            for (String code : stockCodes) {
//                stockSubscribers.computeIfAbsent(code, k -> new HashSet<>()).add(userId);
//            }
//        }
//
//        try {
//            ConnectRes res = new ConnectRes("연결 성공", userId, stockCodes.toString());
//            emitter.send(res);
//        } catch (Exception e) {
//            log.error("subscribeUserWithCodes error: {}", e.getMessage());
//            emitter.complete();
//            userEmitters.remove(userId);
//        }
//
//        return emitter;
//    }
    public SseEmitter subscribeUser(String userId, List<String> stockCodes) {
        // 이미 활성화된 연결이 있다면 재활용
        SseEmitter emitter = userEmitters.get(userId);
        if (emitter == null) {
            emitter = new SseEmitter(Long.MAX_VALUE);
            emitter.onCompletion(() -> userEmitters.remove(userId));
            emitter.onTimeout(() -> userEmitters.remove(userId));
            userEmitters.put(userId, emitter);
        }

        // 기존에 등록된 모든 종목 구독 제거 후 새로운 구독 등록
        removeUserFromAllStocks(userId);
        if (stockCodes != null) {
            for (String code : stockCodes) {
                stockSubscribers.computeIfAbsent(code, k -> new HashSet<>()).add(userId);
            }
        }

        try {
            ConnectRes res = new ConnectRes("연결 성공", userId, stockCodes.toString());
            emitter.send(res);
        } catch (Exception e) {
            log.error("subscribeUserWithCodes error: {}", e.getMessage());
            emitter.complete();
            userEmitters.remove(userId);
        }

        return emitter;
    }

    // 기존에 구독하던 종목에서 제거
    private void removeUserFromAllStocks(String userId) {
        for (Set<String> userSet : stockSubscribers.values()) {
            userSet.remove(userId);
        }
    }

    // Redis 등에서 해당 종목의 실시간 데이터가 들어오면,
    // 이 종목을 구독 중인 유저들을 찾아 SSE로 전송
    public void sendStockData(String stockCode, String eventName, Object data) {
        Set<String> userSet = stockSubscribers.getOrDefault(stockCode, Collections.emptySet());
        if (userSet.isEmpty()) return;

        // JSON 변환
        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("JSON 변환 오류: {}", e.getMessage());
            return;
        }

        for (String userId : userSet) {

            SseEmitter emitter = userEmitters.get(userId);
            if (emitter != null) {
                try {
                    log.info("User ID = {} 에게 전송 데이터 = {}", userId, jsonData);
                    emitter.send(SseEmitter.event()
                            .name(eventName) // 이벤트 이름
                            .data(jsonData)
                            .reconnectTime(10000));
                } catch (Exception e) {
                    log.error("SSE 전송 오류 userId={}, stockCode={}: {}", userId, stockCode, e.getMessage());
                    // 전송 실패 시 연결 제거
                    emitter.complete();
                    userEmitters.remove(userId);
                }
            }
        }
    }

    /**
     * 거래 알림 구독 (userId별로 1개)
     * 만약 거래 알림도 위의 userEmitters에 통합해서 보내고 싶다면,
     * 굳이 별도 맵을 만들 필요 없이, sendStockData처럼 보내면 됩니다.
     */
    public SseEmitter subscribeTradeNotifications(Long userId) {
        // 기존 있으면 제거
        SseEmitter old = tradeSubscribers.get(userId);
        if (old != null) {
            old.complete();
            tradeSubscribers.remove(userId);
        }

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        tradeSubscribers.put(userId, emitter);

        emitter.onCompletion(() -> tradeSubscribers.remove(userId));
        emitter.onTimeout(() -> tradeSubscribers.remove(userId));

        return emitter;
    }

    // 거래 알림 전송 메서드
    @KafkaListener(topics = "tradeResult", groupId = "trade-alert")
    public void sendTradeNotification(ConsumerRecord<String, String> record) {
        TradeResultRes tradeResult = objectMapper.convertValue(record.value(), TradeResultRes.class);

        String statusMessage = "체결 " + ("Success".equals(tradeResult.getMessage()) ? "성공" : "실패");

        // 종목 정보 가져오기
        Stock stock = stockRepository.findByStockCode(tradeResult.getStockCode());

        StringBuilder sb = new StringBuilder();
        sb.append(statusMessage)
                .append(": ")
                .append(stock.getName())   // "삼성전자"
                .append("(").append(tradeResult.getStockCode()).append("), ")
                .append("수량: ").append(tradeResult.getStockAmount()).append("주, ")
                .append("가격: ").append(tradeResult.getStockPrice()).append("원")
                .append("/").append(System.currentTimeMillis());

        // 최종 전송할 메시지
        String finalMessage = sb.toString();

        // SSE 구독자(해당 userId)에게 메시지 전송
        SseEmitter emitter = tradeSubscribers.get(tradeResult.getUserId());
        if (emitter != null) {
            try {
                // JSON이 아니라 문자열 그대로 보내면, 클라이언트 측에서 자연스럽게 표시 가능
                emitter.send(SseEmitter.event().data(finalMessage));
            } catch (Exception e) {
                tradeSubscribers.remove(tradeResult.getUserId());
            }
        }
    }
}