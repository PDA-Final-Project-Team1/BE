package com.team1.etarcade.egg.connector;


import com.team1.etarcade.egg.dto.UserFeignPointResponseDTO;
import com.team1.etarcade.egg.dto.UserFeignStockResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "ET-user", path = "/api/user-stocks")
public interface UserFeignConnector {


    //POST로 유저 보유주식 증가.
    @PostMapping
    void addStockToUser(@RequestBody UserFeignStockResponseDTO requestDTO);

    @GetMapping("/api/users/{userId}/userpoint")
    UserFeignPointResponseDTO getUserPointInfo(@PathVariable("userId") Long userId);


}


