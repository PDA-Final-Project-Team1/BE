package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.dto.UserFavoriteStocksRes;
import com.team1.etuser.user.repository.UserFavoriteStockRepository;
import com.team1.etuser.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFavoriteService {

    private final StockFeignService stockFeignService;
    private final UserRepository userRepository;
    private final UserFavoriteStockRepository userFavoriteStockRepository;

    /**
     * 즐겨찾기 주식 조회
     * @return stockCode, stockName
     */
    public List<UserFavoriteStocksRes> getUserFavoriteStocks() {
        Long id = 1L; // JWT 토큰에서 추출한 값으로 변경

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));
        List<UserFavoriteStocksRes> lists = userFavoriteStockRepository.findByUser(user);

        for (UserFavoriteStocksRes u : lists) {
            String stockName = stockFeignService.getStock(u.getStockCode()).getName();

            u.setStockName(stockName);
        }

        return lists;
    }
}
