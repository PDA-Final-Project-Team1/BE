package com.team1.etcore.stock.controller;

import com.team1.etcore.stock.domain.Stock;
import com.team1.etcore.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/feign")
    public Stock getStocks(@RequestParam String stockCode) {
        return stockService.getStock(stockCode);
    }
}
