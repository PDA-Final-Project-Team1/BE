package com.team1.etarcade.egg.connector;


import com.team1.etarcade.egg.dto.StockPreviousCloseDto;
import com.team1.etarcade.egg.dto.StockPriceDTO;
import com.team1.etarcade.egg.dto.UserFeignPointRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@FeignClient(name = "ET-user" , path = "/api/users" ,contextId = "userEggFeignConnector")
public interface UserFeignConnector {


    //POST로 유저 보유주식 증가.
    @PostMapping("/feign/stock/update")
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
    StockPreviousCloseDto getStockClosingPrice(@PathVariable("stockCode") String stockCode);

}

