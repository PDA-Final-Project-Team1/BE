package com.team1.etarcade.egg.connector;


import com.team1.etarcade.egg.dto.StockAmountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


//Fegin은 stock 값이 들어오면 작성

@FeignClient(name = "ET-core", path = "/api/stocks") // Eureka를 통한 탐색
public interface StockFeignConnector {

    @GetMapping("/fegin")
    StockAmountDTO getStockAmount();

}