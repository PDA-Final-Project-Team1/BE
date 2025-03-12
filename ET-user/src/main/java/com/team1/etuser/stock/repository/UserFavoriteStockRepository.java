package com.team1.etuser.stock.repository;

import com.team1.etuser.stock.dto.StockClosePriceRes;
import com.team1.etuser.user.domain.User;
import com.team1.etuser.stock.domain.UserFavoriteStock;
import com.team1.etuser.stock.dto.UserFavoriteStockRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserFavoriteStockRepository extends JpaRepository<UserFavoriteStock, Long> {

    @Query("SELECT new com.team1.etuser.stock.dto.UserFavoriteStockRes(u.stockCode, '') " +
            "FROM UserFavoriteStock u WHERE u.user = :user")
    List<UserFavoriteStockRes> findByUser(@Param("user") User user);
    @Query("SELECT new com.team1.etuser.stock.dto.StockClosePriceRes(u.stockCode, 0) " +
            "FROM UserStock u WHERE u.user = :user")
    List<StockClosePriceRes> findStockCodeByUser(@Param("user") User user);

    boolean existsByUserAndStockCode(User user, String stockCode);

    Optional<UserFavoriteStock> findByUserAndStockCode(User user, String stockCode);

}
