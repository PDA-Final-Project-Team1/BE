package com.team1.etarcade.egg.connector;


import com.team1.etarcade.egg.dto.UserFeignPointReq;
import com.team1.etarcade.egg.dto.UserFeignPointRes;
import com.team1.etarcade.egg.dto.UserFeignStockResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "ET-user", path = "/api/user-stocks")
public interface UserFeignConnector {


    //POST로 유저 보유주식 증가.
    @PostMapping
    void addStockToUser(@RequestBody UserFeignStockResponseDTO requestDTO);


    //유저포인트 받아오기
    @GetMapping("/api/users/userpoint")
    UserFeignPointRes getUserPointInfo(@RequestHeader("X-Id") Long userId);

    //유저포인트 차감하기
    @PostMapping("/api/users/userpoint")
    UserFeignPointReq setUserPointInfo(@RequestHeader("X-Id") Long userId);

}


