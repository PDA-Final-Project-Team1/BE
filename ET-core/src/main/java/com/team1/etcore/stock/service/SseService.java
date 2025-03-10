package com.team1.etcore.stock.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etcore.stock.dto.TradeResult;
import com.team1.etcore.stock.dto.UserFavoriteStocksRes;
import com.team1.etcore.stock.dto.UserStocksRes;
import com.team1.etcore.trade.client.UserTradeHistoryClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private final Map<String, List<SseEmitter>> interestSubscribers = new ConcurrentHashMap<>();//현재가
    private final Map<String, List<SseEmitter>> portfolioSubscribers = new ConcurrentHashMap<>();//현재가
    private final Map<String, List<SseEmitter>> askBidSubscribers = new ConcurrentHashMap<>();//현재가
    private final Map<String, List<SseEmitter>> curPriceSubscribers = new ConcurrentHashMap<>();//현재가

    // 거래 알림용 SSE 구독자 관리 Map (userId를 key로 사용)
    private final Map<Long, SseEmitter> tradeSubscribers = new ConcurrentHashMap<>();
//    private final UserClient userClient;

    private final UserTradeHistoryClient userTradeHistoryClient;
    public SseService( UserTradeHistoryClient userTradeHistoryClient) {
        this.userTradeHistoryClient = userTradeHistoryClient;
    }

    public SseEmitter getInterestStockPrice(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 200초 타임아웃 설정

        // 사용자의 관심 종목 정보 조회
        ResponseEntity<List<UserFavoriteStocksRes>> response = userTradeHistoryClient.getUserFavoriteStocks(userId);
        List<UserFavoriteStocksRes> userFavoriteStocks = response.getBody();

        // 관심 종목에 대한 구독자 등록
        if (userFavoriteStocks != null) {
            for (UserFavoriteStocksRes stock : userFavoriteStocks) {
                String stockCode = stock.getStockCode(); // 종목 코드 가져오기

                // 관심 종목별 구독자 리스트에 Emitter 추가
                interestSubscribers.computeIfAbsent(stockCode, k -> new ArrayList<>()).add(emitter);

                // SSE 연결이 종료되거나 타임아웃될 때 해당 종목 코드에서 Emitter 제거
                emitter.onCompletion(() -> removeEmitter(interestSubscribers, stockCode, emitter));
                emitter.onTimeout(() -> removeEmitter(interestSubscribers, stockCode, emitter));

                try {
                    emitter.send("연결 성공: " + stockCode);
                } catch (IOException e) {
                    //emitter.completeWithError(e);
                }
            }
        }

        return emitter;
    }

    public void sendToClientsInterestStockPrice(String stockCode, Object data) {

        List<SseEmitter> emitters = interestSubscribers.getOrDefault(stockCode, new ArrayList<>());

        // 리스트에서 제거될 때 반복문 내에서 제거하지 않도록 별도의 리스트로 처리
//        List<SseEmitter> completedEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                // 객체를 JSON 문자열로 변환
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(data);

                // JSON 문자열을 보내기
                emitter.send(SseEmitter.event().data(jsonData));
            } catch (IOException e) {
//                emitter.complete();
//                completedEmitters.add(emitter); // 완료된 emitter를 추적
            }
        }
        // 완료된 emitter들 제거
//        for (SseEmitter emitter : completedEmitters) {
//            removeEmitter(portfolioSubscribers, stockCode, emitter);
//        }
    }

public SseEmitter getPortfolioStockPrice(String userId) {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    System.out.println(portfolioSubscribers.size());

    // 사용자의 포트폴리오 종목 정보 조회
    ResponseEntity<List<UserStocksRes>> response = userTradeHistoryClient.getUserStocks(userId);
    List<UserStocksRes> userStocks = response.getBody();

    // 포트폴리오 종목에 대한 구독자 등록
    if (userStocks != null) {
        for (UserStocksRes stock : userStocks) {
            String stockCode = stock.getStockCode(); // 종목 코드 가져오기

            // 포트폴리오 종목별 구독자 리스트에 Emitter 추가
            portfolioSubscribers.computeIfAbsent(stockCode, k -> new ArrayList<>()).add(emitter);

            // SSE 연결이 종료되거나 타임아웃될 때 해당 종목 코드에서 Emitter 제거
            emitter.onCompletion(() -> removeEmitter(portfolioSubscribers, stockCode, emitter));
            emitter.onTimeout(() -> removeEmitter(portfolioSubscribers, stockCode, emitter));

            // 오류 발생 시 emitter 제거
//            emitter.onError(e -> {
//                removeEmitter(portfolioSubscribers, stockCode, emitter);
//                System.out.println("연결 오류 발생: " + e.getMessage());
//            });

            try {
                emitter.send("연결 성공: " + stockCode);
            } catch (IOException e) {
            }
        }

    }

    return emitter;
}

    public void sendToClientsPortfolioStockPrice(String stockCode, Object data) {
        List<SseEmitter> emitters = portfolioSubscribers.getOrDefault(stockCode, new ArrayList<>());

        // 완료된 emitter를 추적하기 위한 리스트
//        List<SseEmitter> completedEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                // 객체를 JSON 문자열로 변환
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(data);

                // JSON 문자열을 보내기
                emitter.send(SseEmitter.event().data(jsonData));

            } catch (IOException e) {
//                // 오류 발생 시 emitter 완료 처리
//                removeEmitter(portfolioSubscribers, stockCode, emitter); // 리스트에서 제거
//                emitter.complete();
//                completedEmitters.add(emitter); // 완료된 emitter를 추적
            }
        }
//        for (SseEmitter emitter : completedEmitters) {
//            removeEmitter(portfolioSubscribers, stockCode, emitter);
//        }
    }


    // 구독자 목록에서 SSE Emitter 제거하는 메서드
    private void removeEmitter(Map<String, List<SseEmitter>> subscribersMap, String stockCode, SseEmitter emitter) {
        List<SseEmitter> emitters = subscribersMap.get(stockCode);
        if (emitters != null) {
            emitters.remove(emitter);
            // 해당 종목 코드의 구독자가 모두 없으면 해당 키를 삭제
            if (emitters.isEmpty()) {
                subscribersMap.remove(stockCode);
            }
        }
    }



    public SseEmitter getAskBidPrice(String stockCode) {//호가
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        askBidSubscribers.computeIfAbsent(stockCode, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> askBidSubscribers.get(stockCode).remove(emitter));
        emitter.onTimeout(() -> askBidSubscribers.get(stockCode).remove(emitter));

        return emitter;
    }

    public void sendToClientsAskBidStockPrice(String stockCode, Object data) {

        List<SseEmitter> emitters = askBidSubscribers.getOrDefault(stockCode, new ArrayList<>());

        for (SseEmitter emitter : emitters) {
            try {
                // 객체를 JSON 문자열로 변환
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(data);

                // JSON 문자열을 보내기
                emitter.send(SseEmitter.event().data(jsonData));
            } catch (IOException e) {
//                emitter.complete();
            }
        }
    }



    public SseEmitter getStockCurPrice(String stockCode) {
        SseEmitter emitter = new SseEmitter();
        curPriceSubscribers.computeIfAbsent(stockCode, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> curPriceSubscribers.get(stockCode).remove(emitter));
        emitter.onTimeout(() -> curPriceSubscribers.get(stockCode).remove(emitter));

        return emitter;
    }
    public void sendToClientsStockCurPrice(String stockCode, Object data) {

        List<SseEmitter> emitters = curPriceSubscribers.getOrDefault(stockCode, new ArrayList<>());

        for (SseEmitter emitter : emitters) {
            try {
                // 객체를 JSON 문자열로 변환
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(data);

                // JSON 문자열을 보내기
                emitter.send(SseEmitter.event().data(jsonData));
            } catch (IOException e) {
//                emitter.complete();
            }
        }
    }

    // 거래 알림 구독 메서드
    public SseEmitter subscribeTradeNotifications(Long userId) {

        SseEmitter emitter = new SseEmitter(null);


        tradeSubscribers.put(userId, emitter);

        emitter.onCompletion(() -> tradeSubscribers.remove(userId));
        emitter.onTimeout(() -> tradeSubscribers.remove(userId));

        return emitter;
    }

    // 거래 알림 전송 메서드
    @KafkaListener(topics = "tradeResult", groupId = "trade-alert")
    public void sendTradeNotification(ConsumerRecord<String, String> record) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        TradeResult tradeResult = objectMapper.readValue(record.value(), TradeResult.class);

        Long userId = tradeResult.getUserId();

        SseEmitter emitter = tradeSubscribers.get(userId);
        if (emitter != null) {
            try {
                String jsonData = objectMapper.writeValueAsString(tradeResult);
                emitter.send(SseEmitter.event().data(jsonData));
            } catch (IOException e) {
                emitter.complete();
                tradeSubscribers.remove(userId);
            }
        }
    }

}
