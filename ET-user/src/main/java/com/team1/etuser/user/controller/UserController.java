package com.team1.etuser.user.controller;


import com.team1.etuser.user.domain.UserPet;
import com.team1.etuser.user.dto.*;
import com.team1.etuser.user.dto.feign.FeginPointRes;
import com.team1.etuser.user.service.UserAdditionalService;
import com.team1.etuser.user.service.UserFavoriteService;
import com.team1.etuser.user.service.UserPetService;
import com.team1.etuser.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserFavoriteService userFavoriteService;
    private final UserPetService userPetService;
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

    @GetMapping("/history")
    public ResponseEntity<List<UserHistoryRes>> getUserHistory(@RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userService.getUserHistory(userId));
    }

    @GetMapping("/stocks")
    public ResponseEntity<List<UserStocksRes>> getUserStocks(@RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userService.getUserStocks(userId));
    }

    @GetMapping("/favorite")
    public ResponseEntity<List<UserFavoriteStocksRes>> getUserFavoriteStocks(@RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userFavoriteService.getUserFavoriteStocks(userId));
    }

    @PostMapping("/favorite")
    public ResponseEntity<Boolean> addUserFavoriteStock(@RequestBody Map<String, String> stockCode, @RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userFavoriteService.addUserFavoriteStock(stockCode.get("stockCode"), userId));
    }

    @DeleteMapping("/favorite/{stockCode}")
    public ResponseEntity<Boolean> deleteUserFavoriteStock(@PathVariable("stockCode") String stockCode, @RequestHeader("X-Id") String userId) {
        return ResponseEntity.ok(userFavoriteService.deleteUserFavoriteStock(stockCode, userId));
    }

    @GetMapping("/pets")
    public ResponseEntity<List<UserPetResponseDto>> getUserPets(@RequestHeader("X-Id") Long userId) {
        List<UserPetResponseDto> pets = userPetService.getUserPets(userId);
        return ResponseEntity.ok(pets);
    }

    @PostMapping("/pets")
    public ResponseEntity<UserPet> grantPet(@RequestHeader("X-Id") Long userId,
                                         @RequestBody PetGrantRequestDto requestDto) {
        UserPet userPet = userPetService.grantPet(userId, requestDto);
        return ResponseEntity.ok(userPet);
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponseDto> getUserByUid(@RequestParam("uid") String uid) {
        return ResponseEntity.ok(userService.getUserByUid(uid));
    }
    //feign 연결용 포인트 매핑
    @GetMapping("/feign/points")
    public FeginPointRes getUserPoints(@RequestHeader("X-Id") Long userId) {
        log.info("ET-User: 포인트 조회 요청 수신 (사용자: {})", userId);
        return userAdditionalService.getUserPoints(userId);
    }

    @GetMapping("/pets/dex")
    public ResponseEntity<List<UserUniquePetsRes>> getUniquePetsByUser(@RequestHeader("X-Id") Long userId) {
        List<UserUniquePetsRes> pets = userPetService.getUniquePetsByUser(userId);
        return ResponseEntity.ok(pets);
    }
  
    //api 연결용
    @GetMapping("/points")
    public ResponseEntity<PointResponse> userPoints(@RequestHeader("X-Id") Long userId) {
        log.info("API: 포인트 조회 요청 수신 (사용자: {})", userId);
        PointResponse response = userAdditionalService.UserPoints(userId);
        log.info("반환할 PointResponse: {}", response);
        return ResponseEntity.ok(response);
    }

}