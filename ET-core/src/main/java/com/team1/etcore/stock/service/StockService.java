package com.team1.etcore.stock.service;

import com.team1.etcore.stock.domain.Stock;

import com.team1.etcore.stock.dto.StockInfoRes;
import com.team1.etcore.stock.dto.StockRes;
import java.util.Set;

public interface StockService {
    Stock getStock(String stockCode);
    StockInfoRes getRandomStock();
    Set<StockRes> searchStocks(String keyword);
}
