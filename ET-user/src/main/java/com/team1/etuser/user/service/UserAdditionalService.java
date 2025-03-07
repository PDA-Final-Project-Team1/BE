package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.UserAdditionalInfo;
import com.team1.etuser.user.dto.feign.PointRes;
import com.team1.etuser.user.repository.UserAdditionalInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdditionalService {
    private final UserAdditionalInfoRepository userAdditionalInfoRepository;

    /**
     * @return 보유 예치금보다 가격이 높을 시 false
     */
    public boolean enoughDeposit(Long userId, BigDecimal amount) {
        BigDecimal deposit = userAdditionalInfoRepository.findByUserDeposit(userId);
        return deposit.compareTo(amount) >= 0;
    }


    /**
     * 유저 예치금 업데이트
     */
    public boolean updateDeposit(Long userId, BigDecimal amount) {

        Optional<UserAdditionalInfo> userAdditionalInfo = userAdditionalInfoRepository.findById(userId);

        if (userAdditionalInfo.isEmpty()) {
            return false;
        }

        BigDecimal result = userAdditionalInfo.get().getDeposit().add(amount);

        userAdditionalInfo.get().setDeposit(result);

        userAdditionalInfoRepository.save(userAdditionalInfo.get());

        return true;
    }

    /**
     * 유저 포인트 받아오기 및 충분한지 체크
     */

    public PointRes getUserPoints(Long userId) {
        System.out.println("userId 값 확인: " + userId);

        Integer userPoint = userAdditionalInfoRepository.findUserPointByUserId(userId);
        boolean hasEnough = userPoint >= 100;

        log.info("유저 포인트 조회 - userId: {}, points: {}, hasEnough: {}", userId, userPoint, hasEnough);

        return PointRes.builder()
                .point(userPoint)
                .hasEnoughPoints(hasEnough) // 결과 포함
                .build();
    }
}
