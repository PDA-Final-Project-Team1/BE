package com.team1.etarcade.egg.connector;


import com.team1.etarcade.egg.dto.StockAmountDTO;
import com.team1.etarcade.egg.dto.UserFeignPointRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;



@FeignClient(name = "ET-user" , path = "/api/users" ,contextId = "userEggFeignConnector")
public interface UserFeignConnector {


    //POST로 유저 보유주식 증가.
    @PostMapping
    void addStockToUser(@RequestBody StockAmountDTO request);


    //유저포인트 받아오기
    @GetMapping("/feign/points")
    UserFeignPointRes getUserPointInfo(@RequestHeader("X-Id") Long userId);



}


