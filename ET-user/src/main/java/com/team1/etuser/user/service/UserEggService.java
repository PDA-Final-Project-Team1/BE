package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserFavoriteStock;
import com.team1.etuser.user.dto.UserFavoriteStocksRes;
import com.team1.etuser.user.dto.UserPointRes;
import com.team1.etuser.user.repository.UserAdditionalInfoRepository;
import com.team1.etuser.user.repository.UserFavoriteStockRepository;
import com.team1.etuser.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserEggService {

    private final StockFeignService stockFeignService;
    private final UserRepository userRepository;
    private final UserAdditionalInfoRepository useradditionalInfoRepository;

    /**
     * 유저 보유 포인트 조회
     * @return point
     */
    public Optional<UserPointRes> getUserPoint(Long userId) {
//        Long id = 1L; // JWT 토큰에서 추출한 값으로 변경


    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));
        Optional<UserPointRes> userPoint = useradditionalInfoRepository.findPointByUserId(userId);


        return userPoint;
    }


}
