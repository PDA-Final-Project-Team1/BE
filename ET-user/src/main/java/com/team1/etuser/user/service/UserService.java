package com.team1.etuser.user.service;

import com.team1.etuser.stock.domain.TradeStatus;
import com.team1.etuser.user.dto.UserAccountInfoRes;
import com.team1.etuser.stock.dto.UserHistoryRes;
import com.team1.etuser.user.dto.UserInfoRes;
import com.team1.etuser.user.dto.UserSearchRes;
import com.team1.etuser.stock.dto.UserStocksRes;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    boolean isDuplicate(String uid);

    UserInfoRes getUserInfo(String userId);

    UserAccountInfoRes getUserAccountInfo(String userId);

    /**
     * 로그인 User의 거래내역 조회 (페이징 및 거래 상태 필터 적용)
     *
     * @param userId      사용자 아이디
     * @param pageable    페이징 정보
     * @param tradeStatus (옵션) 필터할 거래 상태. null이면 전체 내역 반환.
     * @return 거래 내역 Page
     */
    Page<UserHistoryRes> getUserHistory(String userId, Pageable pageable, TradeStatus tradeStatus);

    List<UserStocksRes> getUserStocks(String userId);

    UserSearchRes getUserByUid(Long id, String uid);


  
}
