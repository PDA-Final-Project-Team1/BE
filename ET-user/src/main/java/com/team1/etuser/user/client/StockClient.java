package com.team1.etuser.user.client;

import com.team1.etuser.user.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ET-core")
public interface StockClient {
    @GetMapping("api/stocks/feign")
    StockDTO getStockInfo(@RequestParam("stockCode") final String stockCode);
}
