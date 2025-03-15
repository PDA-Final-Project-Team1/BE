package com.team1.etcore.stock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    /**
     * 1) 유저별로 단 하나의 SSE를 생성/관리
     * 2) 연결 시 userTradeHistoryClient로 관심/포트폴리오 종목 조회 후 stockSubscribers에 등록
     * 3) 기존에 연결이 있으면 강제로 완료
     */
    public SseEmitter subscribeUser(String userId, List<String> stockCodes) {
        // 기존 Emitter 제거
        SseEmitter oldEmitter = userEmitters.get(userId);
        if (oldEmitter != null) {
            oldEmitter.complete();
            userEmitters.remove(userId);
        }

        // 새 Emitter 생성
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitter.onCompletion(() -> userEmitters.remove(userId));
        emitter.onTimeout(() -> userEmitters.remove(userId));

        userEmitters.put(userId, emitter);


        // 관심 종목 + 보유 종목을 합쳐서 한번에 처리
        // (원한다면 별도 메서드로 분리 가능)
        removeUserFromAllStocks(userId);

        // 새로 전달받은 stockCodes를 구독
        if (stockCodes != null) {
            for (String code : stockCodes) {
                stockSubscribers.computeIfAbsent(code, k -> new HashSet<>()).add(userId);
            }
        }

        try {
            emitter.send("연결 성공: userId=" + userId + ", codes=" + stockCodes);
        } catch (Exception e) {
            log.error("subscribeUserWithCodes error: {}", e.getMessage());
            emitter.complete();
            userEmitters.remove(userId);
        }

        return emitter;
    }

    /** 기존에 구독하던 종목에서 제거 */
    private void removeUserFromAllStocks(String userId) {
        for (Set<String> userSet : stockSubscribers.values()) {
            userSet.remove(userId);
        }
    }

    /**
     * Redis 등에서 해당 종목의 실시간 데이터가 들어오면,
     * 이 종목을 구독 중인 유저들을 찾아 SSE로 전송
     */
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
                    log.info("User ID = {} 에게 전송", userId);
                    emitter.send(SseEmitter.event()
                            .name(eventName) // 이벤트 이름
                            .data(jsonData));
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

    // Kafka Listener로 들어온 거래 결과 알림을 해당 userId의 SSE로 전송
//    @KafkaListener(topics = "tradeResult", groupId = "trade-alert")
//    public void sendTradeNotification(ConsumerRecord<String, String> record) {
//        try {
//            TradeResultRes tradeResult = objectMapper.readValue(record.value(), TradeResultRes.class);
//
//            String statusMessage = "체결 " + ("Success".equals(tradeResult.getMessage()) ? "성공" : "실패");
//            // 간단한 예시로만 작성
//            String finalMessage = statusMessage + " / stockCode=" + tradeResult.getStockCode()
//                    + " / userId=" + tradeResult.getUserId();
//
//            SseEmitter emitter = tradeSubscribers.get(tradeResult.getUserId());
//            if (emitter != null) {
//                emitter.send(SseEmitter.event()
//                        .name("tradeResult")
//                        .data(finalMessage));
//            }
//        } catch (Exception e) {
//            log.error("sendTradeNotification error: {}", e.getMessage());
//        }
//    }
        // 거래 알림 전송 메서드
    @KafkaListener(topics = "tradeResult", groupId = "trade-alert")
    public void sendTradeNotification(ConsumerRecord<String, String> record) {
        // Kafka에서 넘어온 JSON(혹은 LinkedHashMap)을 객체로 변환
        TradeResultRes tradeResult = objectMapper.convertValue(record.value(), TradeResultRes.class);

        // 체결 성공인지 실패인지, 혹은 메시지 필드에 따라 적절히 처리
        String statusMessage = "체결 " + ("Success".equals(tradeResult.getMessage()) ? "성공" : "실패");

        // 종목 정보 가져오기
//        Stock stock = stockRepository.findByStockCode(tradeResult.getStockCode());

        // 가공된 메시지(문자열) 만들기
        // 예: "체결 성공: 삼성전자(005930), 수량: 2주, 가격: 55000원"
        StringBuilder sb = new StringBuilder();
        sb.append(statusMessage)
                .append(": ")
                .append(tradeResult.getName())   // "삼성전자"
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