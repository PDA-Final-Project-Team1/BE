package com.team1.etuser.stock.controller;

import com.team1.etuser.stock.dto.UserHistoryRes;
import com.team1.etuser.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/history")
@RequiredArgsConstructor
public class TradeHistoryController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<UserHistoryRes>> getUserHistory(@RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userService.getUserHistory(userId));
    }
}
