package com.team1.etarcade.egg.connector;


import com.team1.etarcade.egg.dto.UserFeignPointResponseDTO;
import com.team1.etarcade.egg.dto.UserFeignStockResponseDTO;
import com.team1.etarcade.pet.dto.UserPetResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "ET-user", path = "/api/user")
public interface UserFeignConnector {


    //POST로 유저 보유주식 증가.
    @PostMapping
    void addStockToUser(@RequestBody UserFeignStockResponseDTO requestDTO);


    @GetMapping("/points")
    UserFeignPointResponseDTO getUserPointInfo(@RequestHeader("X-Id") Long userId);


}


