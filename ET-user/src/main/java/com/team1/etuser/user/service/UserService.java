package com.team1.etuser.user.service;

import com.team1.etuser.user.dto.UserAccountInfoRes;
import com.team1.etuser.user.dto.UserHistoryRes;
import com.team1.etuser.user.dto.UserInfoRes;
import com.team1.etuser.user.dto.UserResponseDto;
import com.team1.etuser.user.dto.UserStocksRes;

import java.util.List;

public interface UserService {

    boolean isDuplicate(String uid);

    UserInfoRes getUserInfo(String userId);

    UserAccountInfoRes getUserAccountInfo(String userId);

    List<UserHistoryRes> getUserHistory(String userId);

    List<UserStocksRes> getUserStocks(String userId);

    UserResponseDto getUserByUid(String uid);
}
