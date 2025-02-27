package com.team1.etcore.stock.repository;

import com.team1.etcore.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, String> {
    Stock findByStockCode(String stockCode);
}
