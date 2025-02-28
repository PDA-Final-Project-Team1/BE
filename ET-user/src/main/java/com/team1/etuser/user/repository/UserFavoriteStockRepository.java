package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserFavoriteStock;
import com.team1.etuser.user.dto.UserFavoriteStocksRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFavoriteStockRepository extends JpaRepository<UserFavoriteStock, Long> {

    @Query("SELECT new com.team1.etuser.user.dto.UserFavoriteStocksRes(u.stockCode, '') " +
            "FROM UserFavoriteStock u WHERE u.user = :user")
    List<UserFavoriteStocksRes> findByUser(@Param("user") User user);

//    @Query(value = "SELECT stock_code FROM user_favorite_stock WHERE user_id = :userId", nativeQuery = true)
//    List<String> findByUser(@Param("userId") Long userId);
}
