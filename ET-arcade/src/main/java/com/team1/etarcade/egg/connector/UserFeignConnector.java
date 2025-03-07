package com.team1.etarcade.egg.connector;


import com.team1.etarcade.egg.dto.UserFeignPointRes;
import com.team1.etarcade.egg.dto.UserFeignStockResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;



@FeignClient(name = "ET-user" , path = "/api/users" ,contextId = "userEggFeignConnector")
public interface UserFeignConnector {


    //POST로 유저 보유주식 증가.
    @PostMapping
    void addStockToUser(@RequestBody UserFeignStockResponseDTO requestDTO);


    //유저포인트 받아오기
    @GetMapping("/points")
    UserFeignPointRes getUserPointInfo(@RequestHeader("X-Id") Long userId);


    //유저포인트 차감하기
    @PatchMapping("/points")
    void deductUserPoints(@RequestHeader("X-Id") Long userId,@RequestParam("amount") int amount);

}


