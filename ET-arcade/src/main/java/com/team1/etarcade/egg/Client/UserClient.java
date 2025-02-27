package com.team1.etarcade.egg.Client;

import com.team1.etarcade.egg.dto.UserFeignResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "user-service", url = "http://localhost:8081") // 추후 연결.
//public interface UserClient {
    @Component
    public class UserClient{ // 우선 클래스로 구현.
    public int userId = 0;
    public UserFeignResponseDTO getUserInfo(Long userId){
        return new UserFeignResponseDTO(++userId,1000);

    };

    //@GetMapping("/api/users/")
    //UserFeignResponseDTO getUserInfo(@PathVariable("userId") Long userId);
    // 추후 수정.



}
