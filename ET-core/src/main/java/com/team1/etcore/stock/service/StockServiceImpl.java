package com.team1.etcore.stock.service;

import com.team1.etcore.stock.domain.Stock;
import com.team1.etcore.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService{
    private final StockRepository stockRepository;
    @Override
    public Stock getStock(String stockCode) {
        return stockRepository.findByStockCode(stockCode);
    }
}
