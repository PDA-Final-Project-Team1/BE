package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserTradeHistory;
import com.team1.etuser.user.dto.UserHistoryRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserTradeHistoryRepository extends JpaRepository<UserTradeHistory, Long> {
    @Query("SELECT new com.team1.etuser.user.dto.UserHistoryRes(u.stockCode, '', u.price, u.position, u.amount) " +
            "FROM UserTradeHistory u WHERE u.user = :user")
    List<UserHistoryRes> findUserHistory(@Param("user") User user);
}
