package com.team1.etuser.user.controller;


import com.team1.etuser.user.dto.*;
import com.team1.etuser.user.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserAdditionalService userAdditionalService;

    @PostMapping("/duplicate")
    public ResponseEntity<Boolean> isDuplicateUid(@RequestBody Map<String, String> uid) {
        return ResponseEntity.ok(userService.isDuplicate(uid.get("uid")));
    }

    @GetMapping("")
    public ResponseEntity<UserInfoRes> getUserInfo(@RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @GetMapping("/account")
    public ResponseEntity<UserAccountInfoRes> getUserAccountInfo(@RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userService.getUserAccountInfo(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<UserSearchRes> getUserByUid(@RequestHeader("X-Id") Long id, @RequestParam("uid") String uid) {
        return ResponseEntity.ok(userService.getUserByUid(id, uid));
    }

    //api 연결용
    @GetMapping("/points")
    public ResponseEntity<PointRes> userPoints(@RequestHeader("X-Id") Long userId) {
        log.info("API: 포인트 조회 요청 수신 (사용자: {})", userId);
        PointRes response = userAdditionalService.UserPoints(userId);
        log.info("반환할 PointResponse: {}", response);
        return ResponseEntity.ok(response);
    }
}