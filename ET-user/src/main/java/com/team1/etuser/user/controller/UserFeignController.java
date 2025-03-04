package com.team1.etuser.user.controller;

import com.team1.etuser.user.domain.UserTradeHistory;
import com.team1.etuser.user.dto.feign.TradeReq;
import com.team1.etuser.user.dto.feign.TradeRes;
import com.team1.etuser.user.service.UserTradeHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/feign")
@RequiredArgsConstructor
@Slf4j
public class UserFeignController {

    private final UserTradeHistoryService userTradeHistoryService;

    @PostMapping("/trade/order")
    public TradeRes createOrder(@RequestParam("userId") String userId, @RequestBody TradeReq tradeReq) {
//        tradeReq.setUserId(Long.valueOf(userId));
        log.info("ET-user: 주문 생성 요청 수신 (사용자: {}): {}", userId, tradeReq);
        return userTradeHistoryService.createOrder(Long.valueOf(userId), tradeReq);
    }
}
