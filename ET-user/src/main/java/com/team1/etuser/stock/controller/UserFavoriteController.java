package com.team1.etuser.stock.controller;

import com.team1.etuser.stock.dto.UserFavoriteStockRes;
import com.team1.etuser.stock.service.UserFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/favorite")
@RequiredArgsConstructor
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    @GetMapping("")
    public ResponseEntity<List<UserFavoriteStockRes>> getUserFavoriteStocks(@RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userFavoriteService.getUserFavoriteStocks(userId));
    }

    @PostMapping("")
    public ResponseEntity<Boolean> addUserFavoriteStock(@RequestBody Map<String, String> stockCode, @RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userFavoriteService.addUserFavoriteStock(stockCode.get("stockCode"), userId));
    }

    @DeleteMapping("/{stockCode}")
    public ResponseEntity<Boolean> deleteUserFavoriteStock(@PathVariable("stockCode") String stockCode, @RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userFavoriteService.deleteUserFavoriteStock(stockCode, userId));
    }
}
