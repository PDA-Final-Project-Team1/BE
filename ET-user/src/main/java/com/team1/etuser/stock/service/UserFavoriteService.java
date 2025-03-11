package com.team1.etuser.stock.service;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.stock.domain.UserFavoriteStock;
import com.team1.etuser.stock.dto.UserFavoriteStockRes;
import com.team1.etuser.stock.repository.UserFavoriteStockRepository;
import com.team1.etuser.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFavoriteService {

    private final StockFeignService stockFeignService;
    private final UserRepository userRepository;
    private final UserFavoriteStockRepository userFavoriteStockRepository;

    // 즐겨찾기 주식 조회
    public List<UserFavoriteStockRes> getUserFavoriteStocks(String userId) {
//        Long id = 1L; // JWT 토큰에서 추출한 값으로 변경
        Long id = Long.valueOf(userId);

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));
        List<UserFavoriteStockRes> lists = userFavoriteStockRepository.findByUser(user);

        for (UserFavoriteStockRes u : lists) {
            String stockName = stockFeignService.getStock(u.getStockCode()).getName();

            u.setStockName(stockName);
        }

        return lists;
    }

    // @param stockCode 종목의 코드 ex) 005930
    public boolean addUserFavoriteStock(String stockCode, String userId) {
//        Long id = 1L; // JWT 토큰에서 추출한 값으로 변경
        Long id = Long.valueOf(userId);

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        UserFavoriteStock userFavoriteStock = UserFavoriteStock.builder()
                .user(user)
                .stockCode(stockCode)
                .build();

        if (userFavoriteStockRepository.existsByUserAndStockCode(user, stockCode)) {
            throw new RuntimeException("이미 즐겨찾기 되어있는 종목입니다.");
        }

        userFavoriteStockRepository.save(userFavoriteStock);

        return true;
    }

    public boolean deleteUserFavoriteStock(String stockCode, String userId) {
//        Long id = 1L; // JWT 토큰에서 추출한 값으로 변경
        Long id = Long.valueOf(userId);

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        UserFavoriteStock userFavoriteStock = userFavoriteStockRepository.findByUserAndStockCode(user, stockCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 Entity 입니다."));

        userFavoriteStockRepository.delete(userFavoriteStock);

        return true;
    }
}
