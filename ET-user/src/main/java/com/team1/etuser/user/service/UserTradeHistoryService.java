package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.TradeStatus;
import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserTradeHistory;
import com.team1.etuser.user.dto.feign.TradeReq;
import com.team1.etuser.user.dto.feign.TradeRes;
import com.team1.etuser.user.repository.UserRepository;
import com.team1.etuser.user.repository.UserTradeHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTradeHistoryService {
    private final UserTradeHistoryRepository userTradeHistoryRepository;
    private final UserRepository userRepository;

    public TradeRes createOrder(Long userId, TradeReq tradeReq) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        UserTradeHistory trade = UserTradeHistory.builder()
                .amount(tradeReq.getAmount())
                .position(tradeReq.getPosition())
                .price(tradeReq.getPrice())
                .stockCode(tradeReq.getStockCode())
                .user(user)
                .tradeStatus(TradeStatus.PENDING)
                .build();

        UserTradeHistory savedTrade = userTradeHistoryRepository.save(trade);

        return TradeRes.builder()
                .id(savedTrade.getId())
                .createdAt(savedTrade.getCreatedAt())
                .updatedAt(savedTrade.getUpdatedAt())
                .amount(savedTrade.getAmount())
                .position(savedTrade.getPosition())
                .price(savedTrade.getPrice())
                .stockCode(savedTrade.getStockCode())
                .userId(savedTrade.getUser().getId())
                .status(savedTrade.getTradeStatus())
                .build();
    }
}
