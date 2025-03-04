package com.team1.etpipeline.kafka.koreaInvestAPI;

import com.team1.etpipeline.kafka.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
public class KoreaInvestmentWebSocketClient {

    private final ApprovalService approvalService;
    private WebSocketClient tradeWebSocket; // 체결가 웹소켓
    private WebSocketClient askBidWebSocket; // 호가 웹소켓
    private final KafkaProducerService kafkaProducerService;

    private String tradeKey;
    private String askKey;
    private final List<String> trKeys = List.of(
            "005930", "000660", "373220", "207940", "005380", "005935"
//            "000270", "068270", "105560", "035420", "055550", "012330",
//            "005490", "028260", "032830", "010130", "051910", "329180",
//            "138040", "006400", "012450", "000810", "086790", "011200",
//            "035720", "015760", "033780", "066570", "259960", "034020"
    );

    public KoreaInvestmentWebSocketClient(ApprovalService approvalService, KafkaProducerService kafkaProducerService) {
        this.approvalService = approvalService;
        this.kafkaProducerService = kafkaProducerService;
        this.connect();
    }

    public void connect() {
        try {
            this.tradeKey = approvalService.getApprovalKey("trade");
            this.askKey = approvalService.getApprovalKey("ask");

            /**
             * 체결가 현재가
             */
            tradeWebSocket = new WebSocketClient(new URI("ws://ops.koreainvestment.com:21000/tryitout/H0STCNT0")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("WebSocket connection established");
                    sendSubscriptionMessages();//체결가
                }

                @Override
                public void onMessage(String message) {
                    //log.info("Received message: {}", message);
                    //데이터 전처리 작업
                    /**
                     * 토픽: 체결가
                     * 키: 종목번호
                     * 메세지: 데이터
                     */
                    String[] splitData = message.split("\\^");

                    // 필요한 인덱스의 값 추출
                    if(splitData.length > 4) {
                        String stockCode = splitData[0].substring(splitData[0].length() - 6);  // 종목코드의 마지막 6자리
                        String currentPrice = splitData[2];  // 주식 현재가
                        String priceChange = splitData[4];  // 전일 대비 변동 금액
                        String changeRate = splitData[5];  // 전일 대비 변동률

                        String data = stockCode+"^"+currentPrice + "^" + priceChange + "^" + changeRate;

                        kafkaProducerService.sendMessage("H0STCNT0", stockCode, data);
                    }

                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.warn("WebSocket connection closed: {}", reason);
                }

                @Override
                public void onError(Exception ex) {
                    log.error("WebSocket error occurred: {}", ex.getMessage());
                }
            };
            /**
             * 호가
             */
            askBidWebSocket = new WebSocketClient(new URI("ws://ops.koreainvestment.com:21000/tryitout/H0STASP0")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("WebSocket connection established");
                    sendSubscriptionMessages2();//호가
                }

                @Override
                public void onMessage(String message) {
                    /***
                     * 호가 데이터 전처리
                     */
                    if(message.charAt(21) == '^') {
                        // 1. 종목코드 분리
                        String stockCode = message.substring(15, 21);
                        // 2. 종목코드를 제외한 나머지 데이터 추출
                        String datas = message.substring(22);
                        kafkaProducerService.sendMessage("H0STASP0", stockCode, datas);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.warn("WebSocket connection closed: {}", reason);
                }
                @Override
                public void onError(Exception ex) {
                    log.error("WebSocket error occurred: {}", ex.getMessage());
                }
            };

            tradeWebSocket.connect();//체결가
            askBidWebSocket.connect();//호가
        } catch (URISyntaxException e) {
            log.error("Invalid WebSocket URI: {}", e.getMessage());
        }
    }

    private void sendSubscriptionMessages() {

        /**
         *체결가
         */
        try {
            for (String trKey : trKeys) {
                String message = String.format("""
                    {
                        "header": {
                            "approval_key": "%s",
                            "custtype":"P",
                            "tr_type":"1",
                            "content-type":"utf-8"
                        },
                        "body": {
                            "input": {
                                "tr_id": "H0STCNT0",
                                "tr_key": "%s"
                            }
                        }
                    }
                    """, tradeKey,trKey);

                if (tradeWebSocket != null && tradeWebSocket.isOpen()) {
                    tradeWebSocket.send(message.getBytes(StandardCharsets.UTF_8));
                    log.info("Subscription message sent for tr_key {}: {}", trKey, message);
                } else {
                    log.warn("WebSocket is not open");
                }

            }
        } catch (Exception e) {
            log.error("Error sending subscription message: {}", e.getMessage());
        }

    }

    private void sendSubscriptionMessages2() {//호가

        /**
         *호가
         */
        try {
            for (String trKey : trKeys) {

                String message2 = String.format("""
                    {
                        "header": {
                            "approval_key": "%s",
                            "custtype":"P",
                            "tr_type":"1",
                            "content-type":"utf-8"
                        },
                        "body": {
                            "input": {
                                "tr_id": "H0STASP0",
                                "tr_key": "%s"
                            }
                        }
                    }
                    """, askKey,trKey);

                if (askBidWebSocket != null && askBidWebSocket.isOpen()) {
                    askBidWebSocket.send(message2.getBytes(StandardCharsets.UTF_8));
                    log.info("Subscription message sent for tr_key {}: {}", trKey, message2);
                } else {
                    log.warn("WebSocket is not open");
                }

            }
        } catch (Exception e) {
            log.error("Error sending subscription message: {}", e.getMessage());
        }
    }

    public void close() {
        if (tradeWebSocket != null) {
            tradeWebSocket.close();
        }
        if (askBidWebSocket != null) {
            askBidWebSocket.close();
        }
    }
}
