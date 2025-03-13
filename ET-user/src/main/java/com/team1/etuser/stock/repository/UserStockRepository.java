package com.team1.etuser.stock.repository;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.stock.domain.UserStock;
import com.team1.etuser.stock.dto.StockClosePriceRes;
import com.team1.etuser.stock.dto.UserStocksRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserStockRepository extends JpaRepository<UserStock, Long> {

    @Query("SELECT new com.team1.etuser.stock.dto.UserStocksRes(u.stockCode, '', '', u.amount, u.averagePrice) " +
            "FROM UserStock u WHERE u.user = :user")
    List<UserStocksRes> findByUserStocks(@Param("user") User user);

    UserStock findByUserAndStockCode(User user, String stockCode);

    @Query("SELECT new com.team1.etuser.stock.dto.StockClosePriceRes(u.stockCode, 0) " +
            "FROM UserStock u WHERE u.user = :user")
    List<StockClosePriceRes> findStockCodeByUser(@Param("user") User user);

}
