package com.team1.etuser.user.controller;


import com.team1.etuser.user.dto.*;
import com.team1.etuser.user.service.UserFavoriteService;
import com.team1.etuser.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserFavoriteService userFavoriteService;

    @PostMapping("/duplicate")
    public ResponseEntity<Boolean> isDuplicateUid(@RequestBody Map<String, String> uid) {
        return ResponseEntity.ok(userService.isDuplicate(uid.get("uid")));
    }

    @GetMapping("")
    public ResponseEntity<UserInfoRes> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @GetMapping("/account")
    public ResponseEntity<UserAccountInfoRes> getUserAccountInfo() {
        return ResponseEntity.ok(userService.getUserAccountInfo());
    }

    @GetMapping("/history")
    public ResponseEntity<List<UserHistoryRes>> getUserHistory() {
        return ResponseEntity.ok(userService.getUserHistory());
    }

    @GetMapping("/stocks")
    public ResponseEntity<List<UserStocksRes>> getUserStocks() {
        return ResponseEntity.ok(userService.getUserStocks());
    }

    @GetMapping("/favorite")
    public ResponseEntity<List<UserFavoriteStocksRes>> getUserFavoriteStocks() {
        return ResponseEntity.ok(userFavoriteService.getUserFavoriteStocks());
    }

    @PostMapping("/favorite")
    public ResponseEntity<Boolean> addUserFavoriteStock(@RequestBody Map<String, String> stockCode) {
        return ResponseEntity.ok(userFavoriteService.addUserFavoriteStock(stockCode.get("stockCode")));
    }

    @DeleteMapping("/favorite/{stockCode}")
    public ResponseEntity<Boolean> deleteUserFavoriteStock(@PathVariable("stockCode") String stockCode) {
        return ResponseEntity.ok(userFavoriteService.deleteUserFavoriteStock(stockCode));
    }
}