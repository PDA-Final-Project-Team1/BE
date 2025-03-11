package com.team1.etuser.stock.controller;

import com.team1.etuser.stock.domain.Position;
import com.team1.etuser.stock.domain.TradeStatus;
import com.team1.etuser.stock.dto.TradeReq;
import com.team1.etuser.stock.dto.TradeRes;
import com.team1.etuser.stock.service.TradeHistoryService;
import com.team1.etuser.stock.service.UserStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users/feign/trade")
@RequiredArgsConstructor
@Slf4j
public class TradeHistoryFeignController {

    private final TradeHistoryService tradeHistoryService;
    private final UserStockService userStockService;

    @PostMapping("/order")
    public TradeRes createOrder(@RequestParam("userId") Long userId, @RequestBody TradeReq tradeReq) {
        log.info("ET-user: 주문 생성 요청 수신 (사용자: {}): {}", userId, tradeReq);
        return tradeHistoryService.createOrder(userId, tradeReq);
    }

    @PutMapping("/order/update")
    boolean updateHistoryStatus(@RequestParam("orderId") Long historyId,
                                @RequestParam("status") TradeStatus tradeStatus) {
        log.info("ET-user: 주문 생성 요청 수신 (historyId: {}): {}", historyId, tradeStatus.toString());
        return tradeHistoryService.updateHistoryStatus(historyId, tradeStatus);
    }

    @PostMapping("/stock/update")
    boolean updateUserStock(@RequestParam("userId") Long userId,
                            @RequestParam("stockCode") String stockCode,
                            @RequestParam("amount") BigDecimal amount,
                            @RequestParam("price") BigDecimal price,
                            @RequestParam("position") Position position) {
        log.info("ET-user: 보유 주식 요청 수신 (사용자: {}): {}", userId, stockCode);
        return userStockService.updateUserStock(userId, stockCode, amount, price, position);
    }
}
