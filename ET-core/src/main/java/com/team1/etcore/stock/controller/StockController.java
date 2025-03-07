package com.team1.etcore.stock.controller;

import com.team1.etcore.stock.domain.Stock;
import com.team1.etcore.stock.dto.StockResponseDTO;
import com.team1.etcore.stock.service.StockService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/search")
    public ResponseEntity<Set<StockResponseDTO>> search(@RequestParam("query") String query) {
        Set<StockResponseDTO> result = stockService.searchStocks(query);
        return ResponseEntity.ok(result);
    }

//    @GetMapping("/feign/price")
//    public ResponseEntity<Set<StockResponseDTO>> getStockPrice(@RequestParam String stockCode) {
//
//    }

}
