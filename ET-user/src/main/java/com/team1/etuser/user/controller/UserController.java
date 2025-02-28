package com.team1.etuser.user.controller;


import com.team1.etuser.user.dto.UserAccountInfoRes;
import com.team1.etuser.user.dto.UserHistoryRes;
import com.team1.etuser.user.dto.UserInfoRes;
import com.team1.etuser.user.dto.UserStocksRes;
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
}