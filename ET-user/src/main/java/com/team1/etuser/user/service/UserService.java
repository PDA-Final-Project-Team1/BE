package com.team1.etuser.user.service;

import com.team1.etuser.user.dto.*;

import java.util.List;

public interface UserService {

    boolean isDuplicate(String uid);

    UserInfoRes getUserInfo();

    UserAccountInfoRes getUserAccountInfo();

    List<UserHistoryRes> getUserHistory();

    List<UserStocksRes> getUserStocks();
}
