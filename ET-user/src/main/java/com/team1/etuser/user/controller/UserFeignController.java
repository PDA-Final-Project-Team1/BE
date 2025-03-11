package com.team1.etuser.user.controller;

import com.team1.etuser.user.dto.FeginPointRes;
import com.team1.etuser.user.service.UserAdditionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users/feign")
@RequiredArgsConstructor
@Slf4j
public class UserFeignController {

    private final UserAdditionalService userAdditionalService;

    @GetMapping("/account/enough")
    public boolean enoughDeposit(@RequestParam("userId") Long userId, @RequestParam("amount") BigDecimal amount) {
        log.info("ET-user: 금액 조회 요청 수신 (사용자: {}): {}", userId, amount);
        return userAdditionalService.enoughDeposit(userId, amount);
    }



    @PutMapping("/account/update")
    boolean updateDeposit(@RequestParam("userId") Long userId, @RequestParam("amount") BigDecimal amount) {
        log.info("ET-user: 예치금 업데이트 요청 수신 (사용자: {}): {}", userId, amount);
        return userAdditionalService.updateDeposit(userId, amount);
    }

    @PutMapping("/points/update")
    void updateUserPoints(@RequestHeader("X-Id") Long userId,@RequestParam("points") int points) {
        log.info("ET-user: 보유포인트 갱신요청 수신 (사용자: {}): {}", userId, points);
        userAdditionalService.updateUserPoint(userId,points);
    }

    //feign 연결용 포인트 매핑
    @GetMapping("/points")
    public FeginPointRes getUserPoints(@RequestHeader("X-Id") Long userId) {
        log.info("ET-User: 포인트 조회 요청 수신 (사용자: {})", userId);
        return userAdditionalService.getUserPoints(userId);
    }
}
