package com.team1.etarcade.egg.connector;

import com.team1.etarcade.egg.dto.UserFeignPointResponseDTO;
import com.team1.etarcade.egg.dto.UserFeignStockResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFeignConnector { // 우선 클래스로 구현.

    public int userId = 0;

    // 임시 유저 정보 반환
    public UserFeignPointResponseDTO getUserInfo(Long userId) {
        return new UserFeignPointResponseDTO(++userId, 1000);
    }

    // 임시 addStockToUser → Postman에서 확인 가능
    public ResponseEntity<String> addStockToUser(UserFeignStockResponseDTO requestDTO) {
        // 요청 데이터 로깅
        log.info("📌 유저 주식 지급 요청: userId={}, stockName={}, quantity={}",
                requestDTO.getUserId(), requestDTO.getRandomstock(), requestDTO.getQuantity());

        // 정상적으로 실행되었다는 응답 반환
        return ResponseEntity.ok("✅ 주식 지급 완료: " + requestDTO.getRandomstock() + " " + requestDTO.getQuantity() + "주 지급됨");
    }
}



//
//    @FeignClient(name = "ET-user", path = "/api/user-stocks")
//    public interface UserStockClient {
//
//        @PostMapping
//        void addStockToUser(@RequestBody UserStockRequestDTO requestDTO);
//
//        @GetMapping("/api/users/{userId}/userpoint")
////    UserFeignResponseDTO getUserPointInfo(@PathVariable("userId") Long userId);
//    }






//
//    @FeignClient(name = "ET-user", path = "/api/user-stocks")
//    public interface UserStockClient {
//
//        @PostMapping
//        void addStockToUser(@RequestBody UserStockRequestDTO requestDTO);
//
//        @GetMapping("/api/users/{userId}/userpoint")
////    UserFeignResponseDTO getUserPointInfo(@PathVariable("userId") Long userId);
//    }




