package com.team1.etcore.trade.client;

import com.team1.etcore.trade.dto.TradeReq;
import com.team1.etcore.trade.dto.TradeRes;
import com.team1.etcore.trade.dto.TradeStatus;
import com.team1.etcore.trade.dto.Position;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "ET-user")
public interface UserTradeHistoryClient {

    /**
     * 주문 생성
     */
    @PostMapping("/api/users/feign/trade/order")
    TradeRes createOrder(@RequestParam("userId") Long userId, @RequestBody TradeReq tradeReq);


    /**
     * 특정 종목과 체결 가격(주문 가격)으로 미체결(PENDING) 주문 목록을 조회합니다.
     */
    @GetMapping("/api/users/feign/trade/order/pending")
    List<TradeRes> getPendingOrders(@RequestParam("stockCode") String stockCode,
                                    @RequestParam("tradePrice") BigDecimal tradePrice);

    /**
     * 주문상태 업데이트
     */
    @PatchMapping("/api/users/feign/trade/order/update")
    boolean updateOrderStatus(@RequestParam("userId") Long userId,
                              @RequestParam("orderId") Long orderId,
                              @RequestParam("status") TradeStatus tradeStatus);


    /**
     * 사용자 예치금 차감 API (BUY)
     */
    @PatchMapping("/api/users/feign/account/deduct")
    boolean deductDeposit(@RequestParam("userId") Long userId,
                          @RequestParam("amount") BigDecimal amount);
    /**
     * 사용자 예치금 충전 API (SELL)
     */
    @PatchMapping("/api/users/feign/account/credit")
    boolean creditDeposit(@RequestParam("userId") Long userId,
                          @RequestParam("amount") BigDecimal amount);

    /**
     * 보유 주식 업데이트 API
     */
    @PatchMapping("/api/users/feign /stock/update")
    boolean updateUserStock(@RequestParam("userId") Long userId,
                            @RequestParam("stockCode") String stockCode,
                            @RequestParam("amount") int amount,
                            @RequestParam("price") BigDecimal price,
                            @RequestParam("position") Position position);
}
