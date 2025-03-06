package com.team1.etarcade.egg.service;


import com.team1.etarcade.egg.connector.UserFeignConnector;
import com.team1.etarcade.egg.domain.Egg;
import com.team1.etarcade.egg.dto.EggCreateRes;
import com.team1.etarcade.egg.dto.UserFeignPointRes;
import com.team1.etarcade.egg.repository.EggRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EggService {


    private final EggRepository eggRepository;
    private final UserFeignConnector userFeignConnector;
    private static final Duration INCUBATION_DURATION = Duration.ofMinutes(1); // 부화 시간 24시간
    private final RewardStockService rewardStockService;

    @Transactional
    //알 얻는 과정
    public EggCreateRes acquireEgg(Long userId) { //알얻기
        // FeignClient를 통해 사용자 정보 조회
        UserFeignPointRes userInfo = userFeignConnector.getUserPointInfo(userId);

        //유저가 가진 포인트 조회
        if (userInfo.getHasEnoughPoints()) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

        // 알 생성
        Egg newEgg = Egg.builder()
                .userId(userId)
                .isHatchable(false)
                .isHatched(false)
                .build();
        Egg savedEgg = eggRepository.save(newEgg);

        // 포인트 차감 (별도의 Feign API 필요할 수 있음)
        // userClient.deductPoints(userId, 100);  <- 추후 구현


        //알DTO 반환
        return new EggCreateRes(
                savedEgg.getId(),
                userId,
                "부화 대기 중",
                "부화 안 됨",
                savedEgg.getCreatedAt(),
                calculateTimeRemaining(savedEgg.getCreatedAt())
        );
    }


    // 유저의 모든 알 조회
    public List<EggCreateRes> getAllEggs(Long userId) {



        return eggRepository.findByUserId(userId).stream()
                .map(egg -> {updateEggStatus(egg);


                    return new EggCreateRes(

                            egg.getId(),
                            egg.getUserId(),

                            egg.isHatchable() ? "부화 가능" : "부화 불가",
                            egg.isHatched() ? "부화완료됨" : "부화중임",
                            egg.getCreatedAt(),
                            calculateTimeRemaining(egg.getCreatedAt())

                    );})
                .toList();

    }

    //알 상태 갱신 함수.
    private void updateEggStatus(Egg egg) {
        LocalDateTime expirationTime = egg.getCreatedAt().plus(INCUBATION_DURATION);
        if (LocalDateTime.now().isAfter(expirationTime) && !egg.isHatched()) {
            egg.setHatchable(true);
            egg.setHatched(true);
            eggRepository.save(egg); // 상태 변경 후 DB에 업데이트
        }
    }


    // 남은 시간 계산 함수
    private String calculateTimeRemaining(LocalDateTime createdAt) {
        LocalDateTime expirationTime = createdAt.plus(INCUBATION_DURATION);
        Duration remaining = Duration.between(LocalDateTime.now(), expirationTime);
        if (remaining.isNegative()) {
            return "00:00:00"; // 이미 부화됨
        }

        return String.format("%02d:%02d:%02d",
                remaining.toHours(),
                remaining.toMinutesPart(),
                remaining.toSecondsPart());
    }


    //알 부화 및 주식 지급 로직
    public void hatchEggAndGiveStock(Long userId, Long eggId) {
        Egg egg = eggRepository.findById(eggId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알을 찾을 수 없습니다."));

        if (!egg.isHatchable() || egg.isHatched()) {
            throw new IllegalStateException("부화할 수 없는 알입니다.");
        }

        // 1️⃣ 주식 지급
        rewardStockService.giveRandomStockToUser(userId, 10000); // 10,000원어치 주식 지급

        // 2️⃣ 알 삭제
        eggRepository.delete(egg);
    }
}