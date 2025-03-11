package com.team1.etarcade.egg.connector;


import com.team1.etarcade.egg.dto.StockNameAndCodeDTO;
import com.team1.etarcade.egg.dto.StockPriceDTO;
import com.team1.etarcade.egg.dto.UserFeignPointRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@FeignClient(name = "ET-core" , path = "/api/stocks" ,contextId = "StockEggFeignConnector")
public interface StockFeignConnector {


    @GetMapping("/randomstock")
    StockNameAndCodeDTO getRandomStock();



}

