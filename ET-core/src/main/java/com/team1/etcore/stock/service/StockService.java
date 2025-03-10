package com.team1.etcore.stock.service;

import com.team1.etcore.stock.domain.Stock;

import com.team1.etcore.stock.dto.StockNameAndCodeDTO;
import com.team1.etcore.stock.dto.StockResponseDTO;
import java.util.Set;

public interface StockService {
    Stock getStock(String stockCode);
    Set<StockResponseDTO> searchStocks(String keyword);
    StockNameAndCodeDTO getRandomStock();
}
