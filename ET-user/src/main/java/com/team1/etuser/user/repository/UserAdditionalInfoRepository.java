package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.UserAdditionalInfo;
import com.team1.etuser.user.dto.UserAccountInfoRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserAdditionalInfoRepository extends JpaRepository<UserAdditionalInfo, Long> {
    @Query("SELECT new com.team1.etuser.user.dto.UserAccountInfoRes(u.deposit, u.account) " +
            "FROM UserAdditionalInfo u WHERE u.user_id = :userId")
    Optional<UserAccountInfoRes> findByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT deposit FROM user_additional_info u WHERE u.user_id = :userId",nativeQuery = true)
    BigDecimal findByUserDeposit(@Param("userId") Long userId);
}
