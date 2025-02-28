package com.team1.etuser.user.service;

import com.team1.etuser.user.client.StockClient;
import com.team1.etuser.user.dto.StockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockFeignService {
    private final StockClient stockClient;

    public StockDTO getStock(String stockCode) {
        return stockClient.getStockInfo(stockCode);
    }
}
