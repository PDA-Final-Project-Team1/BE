package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.Position;
import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserStock;
import com.team1.etuser.user.repository.UserRepository;
import com.team1.etuser.user.repository.UserStockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserStockService {
    private final UserStockRepository userStockRepository;
    private final UserRepository userRepository;

    public boolean updateUserStock(Long userId, String stockCode, int amount, BigDecimal price, Position position) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));
        // 유저 보유 주식이 존재하는지 조회
        UserStock userStock = userStockRepository.findByUserAndStockCode(user, stockCode);

        if (position.equals(Position.BUY)) {
            if (userStock == null) {
                // 보유 주식이 존재하지 않으면 새로운 Row 생성
                userStock = UserStock.builder()
                        .amount(amount)
                        .averagePrice(price)
                        .stockCode(stockCode)
                        .user(user)
                        .build();
            } else {
                // 기존 보유 수량과 평단가를 기준으로 새로운 평단가 계산
                int existingAmount = userStock.getAmount();
                BigDecimal existingAveragePrice = userStock.getAveragePrice();

                int newAmount = existingAmount + amount;
                // (기존 평단가 * 기존 개수) + (새로운 가격 + 새로운 개수)
                BigDecimal totalCost = existingAveragePrice.multiply(BigDecimal.valueOf(existingAmount))
                        .add(price.multiply(BigDecimal.valueOf(amount)));

                // 새로운 평단가
                BigDecimal newAveragePrice = totalCost.divide(BigDecimal.valueOf(newAmount), MathContext.DECIMAL128);

                // setter를 사용하여 amount와 averagePrice만 수정
                userStock.setAmount(newAmount);
                userStock.setAveragePrice(newAveragePrice);
            }
            userStockRepository.save(userStock);
            return true;
        } else if (position.equals(Position.SELL)) {
            if (userStock == null) {
                throw new IllegalStateException("보유 주식이 없습니다.");
            }

            int existingAmount = userStock.getAmount();
            if (existingAmount < amount) {
                throw new IllegalArgumentException("판매할 주식 수량이 보유 수량보다 많습니다.");
            }

            int newAmount = existingAmount - amount;
            if (newAmount == 0) {
                // 매도 후 수량이 0이면 해당 row 삭제
                userStockRepository.delete(userStock);
            } else {
                // 매도 후 남은 수량 업데이트 (평단가는 그대로 유지)
                userStock.setAmount(newAmount);
                userStockRepository.save(userStock);
            }
            return true;
        } else {
            throw new IllegalArgumentException("알 수 없는 포지션입니다: " + position);
        }
    }
}
