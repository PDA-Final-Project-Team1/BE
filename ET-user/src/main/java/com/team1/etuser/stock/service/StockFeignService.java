package com.team1.etuser.stock.service;

import com.team1.etuser.user.client.StockClient;
import com.team1.etuser.stock.dto.StockRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockFeignService {
    private final StockClient stockClient;

    public StockRes getStock(String stockCode) {
        return stockClient.getStockInfo(stockCode);
    }
}
