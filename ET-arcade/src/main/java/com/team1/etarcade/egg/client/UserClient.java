package com.team1.etarcade.egg.client;


import com.team1.etarcade.egg.dto.StockClosePriceRes;
import com.team1.etarcade.egg.dto.UserFeignPointRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@FeignClient(name = "ET-user" , path = "/api/users", contextId = "userClient")
public interface UserClient {


    //POST로 유저 보유주식 증가.
    @PostMapping("/feign/trade/stock/update")
    boolean updateUserStock(@RequestParam("userId") Long userId,
                            @RequestParam("stockCode") String stockCode,
                            @RequestParam("amount") BigDecimal amount,
                            @RequestParam("price") BigDecimal price,
                            @RequestParam("position") String position);

    //유저포인트 받아오기
    @GetMapping("/feign/points")
    UserFeignPointRes getUserPointInfo(@RequestHeader("X-Id") Long userId);

    //주식 넣으면 종가 가져오기~
    @GetMapping("/stocks/closing-price/{stockCode}")
    StockClosePriceRes getStockClosingPrice(@PathVariable("stockCode") String stockCode);

}

