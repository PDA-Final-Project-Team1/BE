package com.team1.etarcade.egg.client;


import com.team1.etarcade.egg.dto.StockInfoRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
@FeignClient(name = "ET-core", path = "/api/stocks", contextId = "stockClient") // Eureka를 통한 탐색
public interface StockClient {

    @GetMapping("/randomstock")
    StockInfoRes getRandomStock();

}