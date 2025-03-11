package com.team1.etuser.stock.controller;

import com.team1.etuser.stock.dto.StockClosePriceRes;
import com.team1.etuser.stock.dto.UserStocksRes;
import com.team1.etuser.stock.service.UserStockService;
import com.team1.etuser.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/stocks")
@RequiredArgsConstructor
@Slf4j
public class UserStockController {

    private final UserService userService;
    private final UserStockService userStockService;

    @GetMapping("")
    public ResponseEntity<List<UserStocksRes>> getUserStocks(@RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userService.getUserStocks(userId));
    }

    // 보유주식의 전날 종가 조회
    @GetMapping("/closing-price")
    public ResponseEntity<List<StockClosePriceRes>> getUserStockClosingPrice(@RequestHeader("X-Id") Long userId) {
        log.info("보유 종목들의 전날 종가 조회 (사용자: {})", userId);
        return ResponseEntity.ok(userStockService.getUserStockClosingPrice(userId));
    }

    // 특정 종목의 전날 종가 조회
    @GetMapping("/closing-price/{stockCode}")
    public ResponseEntity<StockClosePriceRes> getStockClosingPrice(@PathVariable("stockCode") String stockCode) {
        log.info("특정 종목의 전날 종가 조회 (종목코드 {})", stockCode);
        return ResponseEntity.ok(userStockService.getStockClosingPrice(stockCode));
    }
}
