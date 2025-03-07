package com.team1.etarcade.egg.connector;


import com.team1.etarcade.egg.dto.StockPriceResponseDTO;
import com.team1.etarcade.egg.dto.UserFeignStockResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


//Fegin은 stock 값이 들어오면 작성

@FeignClient(name = "ET-core", path = "/api/stocks") // Eureka를 통한 탐색
public interface StockFeignConnector {

    @GetMapping("/fegin")
    StockPriceResponseDTO getStockPrice(@RequestParam String stockCode);

}