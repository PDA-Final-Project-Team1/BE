package com.team1.etuser.user.service;

import com.team1.etuser.user.dto.UserAccountInfoRes;
import com.team1.etuser.user.dto.UserHistoryRes;
import com.team1.etuser.user.dto.UserInfoRes;
import com.team1.etuser.user.dto.UserStocksRes;

import java.util.List;

public interface UserService {

    boolean isDuplicate(String uid);

    UserInfoRes getUserInfo();

    UserAccountInfoRes getUserAccountInfo();

    List<UserHistoryRes> getUserHistory();

    List<UserStocksRes> getUserStocks();
}
