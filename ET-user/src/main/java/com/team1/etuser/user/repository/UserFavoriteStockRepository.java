package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserFavoriteStock;
import com.team1.etuser.user.dto.UserFavoriteStocksRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserFavoriteStockRepository extends JpaRepository<UserFavoriteStock, Long> {

    @Query("SELECT new com.team1.etuser.user.dto.UserFavoriteStocksRes(u.stockCode, '') " +
            "FROM UserFavoriteStock u WHERE u.user = :user")
    List<UserFavoriteStocksRes> findByUser(@Param("user") User user);

    boolean existsByUserAndStockCode(User user, String stockCode);

    Optional<UserFavoriteStock> findByUserAndStockCode(User user, String stockCode);
}
