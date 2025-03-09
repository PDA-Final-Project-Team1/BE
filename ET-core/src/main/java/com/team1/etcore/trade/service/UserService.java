package com.team1.etcore.trade.service;

import com.team1.etcore.trade.client.UserTradeHistoryClient;
import com.team1.etcore.trade.dto.UserFavoriteStocksRes;
import com.team1.etcore.trade.dto.UserStocksRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserTradeHistoryClient userTradeHistoryClientClientClient;

    public List<UserStocksRes> getUserStocks(Long userId) {
        return userTradeHistoryClientClientClient.getUserStocks(userId);
    }

    public List<UserFavoriteStocksRes> getUserFavoriteStocks(Long userId) {
        return userTradeHistoryClientClientClient.getUserFavoriteStocks(userId);
    }
}
