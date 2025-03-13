package com.team1.etuser.stock.repository;

import com.team1.etuser.stock.domain.TradeStatus;
import com.team1.etuser.user.domain.User;
import com.team1.etuser.stock.domain.UserTradeHistory;
import com.team1.etuser.stock.dto.UserHistoryRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserTradeHistoryRepository extends JpaRepository<UserTradeHistory, Long> {
    @Query("SELECT new com.team1.etuser.stock.dto.UserHistoryRes(u.id, '', u.stockCode, '', u.price, u.position, u.amount, u.createdAt, u.updatedAt, u.tradeStatus) " +
            "FROM UserTradeHistory u " +
            "WHERE u.user = :user " +
            "AND (:tradeStatus IS NULL OR u.tradeStatus = :tradeStatus) " +
            "ORDER BY u.updatedAt DESC")
    Page<UserHistoryRes> findUserHistoryByTradeStatus(@Param("user") User user,
                                                      @Param("tradeStatus") TradeStatus tradeStatus,
                                                      Pageable pageable);
}
