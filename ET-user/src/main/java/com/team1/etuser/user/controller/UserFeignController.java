package com.team1.etuser.user.controller;

import com.team1.etuser.user.domain.Position;
import com.team1.etuser.user.domain.TradeStatus;
import com.team1.etuser.user.domain.UserTradeHistory;
import com.team1.etuser.user.dto.feign.PointRes;
import com.team1.etuser.user.dto.feign.TradeReq;
import com.team1.etuser.user.dto.feign.TradeRes;
import com.team1.etuser.user.service.UserAdditionalService;
import com.team1.etuser.user.service.UserStockService;
import com.team1.etuser.user.service.UserTradeHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users/feign")
@RequiredArgsConstructor
@Slf4j
public class UserFeignController {

    private final UserTradeHistoryService userTradeHistoryService;
    private final UserAdditionalService userAdditionalService;
    private final UserStockService userStockService;

    @GetMapping("/account/enough")
    public boolean enoughDeposit(@RequestParam("userId") Long userId, @RequestParam("amount") BigDecimal amount) {
        log.info("ET-user: 금액 조회 요청 수신 (사용자: {}): {}", userId, amount);
        return userAdditionalService.enoughDeposit(userId, amount);
    }

    @PostMapping("/trade/order")
    public TradeRes createOrder(@RequestParam("userId") Long userId, @RequestBody TradeReq tradeReq) {
//        tradeReq.setUserId(Long.valueOf(userId));
        log.info("ET-user: 주문 생성 요청 수신 (사용자: {}): {}", userId, tradeReq);
        return userTradeHistoryService.createOrder(userId, tradeReq);
    }

    @PutMapping("/trade/order/update")
    boolean updateHistoryStatus(@RequestParam("orderId") Long historyId,
                              @RequestParam("status") TradeStatus tradeStatus) {
        log.info("ET-user: 주문 생성 요청 수신 (historyId: {}): {}", historyId, tradeStatus.toString());
        return userTradeHistoryService.updateHistoryStatus(historyId, tradeStatus);
    }

    @PutMapping("/account/update")
    boolean updateDeposit(@RequestParam("userId") Long userId, @RequestParam("amount") BigDecimal amount) {
        log.info("ET-user: 예치금 업데이트 요청 수신 (사용자: {}): {}", userId, amount);
        return userAdditionalService.updateDeposit(userId, amount);
    }

    @PostMapping("/stock/update")
    boolean updateUserStock(@RequestParam("userId") Long userId,
                            @RequestParam("stockCode") String stockCode,
                            @RequestParam("amount") int amount,
                            @RequestParam("price") BigDecimal price,
                            @RequestParam("position") Position position) {
        log.info("ET-user: 보유 주식 요청 수신 (사용자: {}): {}", userId, stockCode);
        return userStockService.updateUserStock(userId, stockCode, amount, price, position);
    }


}
