package com.team1.etcore.trade.controller;

import com.team1.etcore.trade.dto.*;
import com.team1.etcore.trade.service.TradeService;
import com.team1.etcore.trade.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
@Slf4j
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;
    private final UserService userService;

    // 매수 주문 생성 API
    @PostMapping("/buy")
    public TradeRes buy(@RequestHeader("X-Id") String userId, @RequestBody TradeReq tradeReq) {
        try {
            tradeReq.setPosition(Position.BUY);
            log.info("매수 주문 요청 수신 (사용자: {}): {}", userId, tradeReq);
            return tradeService.createOrder(Long.valueOf(userId), tradeReq);
        } catch (Exception e) {
            log.error("매수 주문 처리 중 오류 발생 (사용자: {}): {}", userId, e.getMessage(), e);
            throw new RuntimeException("매수 주문 생성 실패", e);
        }
    }

    // 매도 주문 생성 API
    @PostMapping("/sell")
    public TradeRes sell(@RequestHeader("X-Id") String userId, @RequestBody TradeReq tradeReq) {
        try {
            tradeReq.setPosition(Position.SELL);
            log.info("매도 주문 요청 수신 (사용자: {}): {}", userId, tradeReq);
            return tradeService.createOrder(Long.valueOf(userId), tradeReq);
        } catch (Exception e) {
            log.error("매도 주문 처리 중 오류 발생 (사용자: {}): {}", userId, e.getMessage(), e);
            throw new RuntimeException("매도 주문 생성 실패", e);
        }
    }

    @PostMapping("/cancel")
    public void cancel(@RequestHeader("X-Id") String userId, @RequestBody TradeCancelReq tradeCancelReq) {
        try {
            tradeService.cancelOrder(tradeCancelReq.getTradeId(), tradeCancelReq.getPosition(), tradeCancelReq.getStockCode(), tradeCancelReq.getPrice());
            log.info("거래 취소 성공: userId={}, tradeId={}", userId, tradeCancelReq.getTradeId());
        } catch (Exception e) {
            log.error("거래 취소 중 오류 발생: userId={}, tradeId={}, error={}", userId, tradeCancelReq.getTradeId(), e.getMessage(), e);
            throw new RuntimeException("거래 취소 중 오류가 발생했습니다.", e);
        }
    }

    @GetMapping("/users/stocks")
    public ResponseEntity<List<UserStocksRes>> getUserStocks(@RequestHeader("X-Id") Long userId) {
        return ResponseEntity.ok(userService.getUserStocks(userId));
    }

    @GetMapping("/users/favorite")
    public ResponseEntity<List<UserFavoriteStocksRes>> getUserFavoriteStocks(@RequestHeader("X-Id") Long userId) {
        return ResponseEntity.ok(userService.getUserFavoriteStocks(userId));
    }
}
