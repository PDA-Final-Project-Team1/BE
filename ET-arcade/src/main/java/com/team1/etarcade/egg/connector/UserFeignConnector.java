package com.team1.etarcade.egg.connector;

import com.team1.etarcade.egg.dto.UserFeignResponseDTO;
import org.springframework.stereotype.Component;

//@FeignClient(name = "ET-user", url = "http://localhost:8081") // 추후 연결.
//public interface UserClient {
    @Component
    public class UserFeignConnector { // 우선 클래스로 구현.
    public int userId = 0;
   public UserFeignResponseDTO getUserInfo(Long userId){
       return new UserFeignResponseDTO(++userId,1000);
    };

//    @GetMapping("/api/users/{userId}/userpoint")
//    UserFeignResponseDTO getUserPointInfo(@PathVariable("userId") Long userId);




}
