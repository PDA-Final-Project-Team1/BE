package com.team1.etcore.stock.repository;

import com.team1.etcore.stock.domain.Stock;
import com.team1.etcore.stock.dto.StockInfoRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, String> {
    Stock findByStockCode(String stockCode);

    @Query(value = "SELECT * FROM stock ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Stock getRandomStock();
}
