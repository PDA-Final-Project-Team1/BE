package com.team1.etuser.stock.controller;

import com.team1.etuser.stock.domain.TradeStatus;
import com.team1.etuser.stock.dto.TradeCountRes;
import com.team1.etuser.stock.dto.UserHistoryRes;
import com.team1.etuser.stock.service.TradeHistoryService;
import com.team1.etuser.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/history")
@RequiredArgsConstructor
public class TradeHistoryController {

    private final UserService userService;
    private final TradeHistoryService tradeHistoryService;

    @GetMapping("")
    public ResponseEntity<Page<UserHistoryRes>> getUserHistory(
            @RequestHeader("X-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TradeStatus tradeStatus) {

        Page<UserHistoryRes> history = userService.getUserHistory(
                userId,
                PageRequest.of(page, size),
                tradeStatus
        );
        return ResponseEntity.ok(history);
    }

    @GetMapping("/count")
    public ResponseEntity<TradeCountRes> getTradeCount(@RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(tradeHistoryService.getTradeCount(userId));
    }

}
