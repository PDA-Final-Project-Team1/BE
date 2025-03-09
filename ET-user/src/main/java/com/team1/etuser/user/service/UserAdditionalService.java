package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.UserAdditionalInfo;
import com.team1.etuser.user.dto.PointResponse;
import com.team1.etuser.user.dto.feign.FeginPointRes;
import com.team1.etuser.user.repository.UserAdditionalInfoRepository;
import jakarta.persistence.EntityNotFoundException;
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
     * FEGIN- 유저 포인트 받아오기 및 충분한지 체크
     */

    public FeginPointRes getUserPoints(Long userId) {
        System.out.println("userId 값 확인: " + userId);

        Integer userPoint = userAdditionalInfoRepository.findUserPointByUserId(userId);
        boolean hasEnough = userPoint >= 500;

        log.info("유저 포인트 조회 - userId: {}, points: {}, hasEnough: {}", userId, userPoint, hasEnough);

        //포인트 차감.
        if (hasEnough) {
            BigDecimal newPoints = new BigDecimal(userPoint).subtract(BigDecimal.valueOf(500));
            userAdditionalInfoRepository.updateUserPoints(userId, newPoints.intValue()); // 포인트 업데이트 쿼리 필요
        }

        return FeginPointRes.builder()
                .point(userPoint)
                .hasEnoughPoints(hasEnough) // 결과 포함
                .build();
    }

    // API 연결용 유저포인트 함수
    public PointResponse UserPoints(Long userId) {
        System.out.println("userId 값 확인: " + userId);
        int userPoint = userAdditionalInfoRepository.findUserPointByUserId(userId);
        return PointResponse.builder()
                .point(userPoint)
                .build();
    }

    //유저 포인트 업데이트
    public void updateUserPoint(Long userId, int amount) {

        UserAdditionalInfo userAdditionalInfo = userAdditionalInfoRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 추가정보를 찾을 수 없습니다."));

        int userPoint = userAdditionalInfoRepository.findUserPointByUserId(userId);
        int newPoints = userPoint + amount;
        userAdditionalInfoRepository.updateUserPoints(userId, newPoints); // 포인트 업데이트 쿼리 필요
    }
}

