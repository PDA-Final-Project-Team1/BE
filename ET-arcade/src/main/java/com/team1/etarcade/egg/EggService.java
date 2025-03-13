package com.team1.etarcade.egg;


import com.team1.etarcade.egg.client.StockClient;
import com.team1.etarcade.egg.client.UserClient;
import com.team1.etarcade.egg.domain.Egg;
import com.team1.etarcade.egg.dto.*;
import com.team1.etarcade.egg.dto.EggCreateRes;
import com.team1.etarcade.egg.dto.UserFeignPointRes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EggService {


    private static final Logger log = LoggerFactory.getLogger(EggService.class);
    private final EggRepository eggRepository;
    private final UserClient userClient;
    private static final Duration INCUBATION_DURATION = Duration.ofMinutes(1); // 부화 시간 24시간
    private final StockClient stockClient;
    private final TaskScheduler taskScheduler;

    @Transactional
    //알 얻는 과정
    public EggCreateRes acquireEgg(Long userId) { // 알 얻기
        // FeignClient를 통해 사용자 정보 조회
        UserFeignPointRes userInfo = userClient.getUserPointInfo(userId);

        //유저가 가진 포인트 조회 및 차감.
        if (!userInfo.getHasEnoughPoints()) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

        // 알 생성
        Egg newEgg = Egg.builder()
                .userId(userId)
                .isHatchable(false)
                .isHatched(false)
                .build();
        Egg savedEgg = eggRepository.save(newEgg);

        // 알 부화 스케줄링
        scheduleHatchableUpdate(savedEgg.getId());

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

    private void scheduleHatchableUpdate(Long eggId) {
        // Deprecated된 Date 대신, Instant로 예약 시간을 계산
        Instant futureTime = Instant.now().plus(INCUBATION_DURATION);

        taskScheduler.schedule(
                () -> {
                    Egg egg = eggRepository.findById(eggId).orElse(null);
                    if (egg != null) {
                        egg.setHatchable(true);
                        eggRepository.save(egg);
                        log.info("알(" + eggId + ")이 부화 가능 상태로 업데이트되었습니다.");
                    }
                },
                futureTime
        );
    }


    // 유저의 모든 알 조회
    public List<EggCreateRes> getAllEggs(Long userId) {


        return eggRepository.findByUserId(userId).stream()
                .map(egg -> {
//                    updateEggStatus(egg);
                    return new EggCreateRes(

                            egg.getId(),
                            egg.getUserId(),

                            egg.isHatchable() ? "부화 가능" : "부화 불가",
                            egg.isHatched() ? "부화완료됨" : "부화중임",
                            egg.getCreatedAt(),
                            calculateTimeRemaining(egg.getCreatedAt())

                    );
                })
                .toList();

    }

    //알 상태 갱신 함수.
//    private void updateEggStatus(Egg egg) {
//        LocalDateTime expirationTime = egg.getCreatedAt().plus(INCUBATION_DURATION);
//        if (LocalDateTime.now().isAfter(expirationTime) && !egg.isHatched()) {
//            egg.setHatchable(true);
////            egg.setHatched(true);
//            eggRepository.save(egg); // 상태 변경 후 DB에 업데이트
//        }
//    }


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

    //유저의 알인지 확인
    private boolean isUserEgg(Long userId, Long eggId) {
        return eggRepository.existsByIdAndUserId(eggId, userId);
    }

    //알 부화 및 주식 지급 로직
    @Transactional
    public EggHatchingRes hatchEggAndRewardStock(Long userId, Long eggId) {

        if (!isUserEgg(userId, eggId)) {
            throw new IllegalStateException("해당 알은 사용자의 것이 아닙니다.");
        }

        //알이 존재하는지 확인
        Egg egg = eggRepository.findById(eggId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알을 찾을 수 없습니다."));

        log.info("EGG = " + egg.toString());
        if (!egg.isHatchable() || egg.isHatched()) {
            throw new IllegalStateException("부화할 수 없는 알입니다.");
        }
        egg.setHatched(true);
        log.info("setHatched 부화 완료 - eggId: {}", eggId);
        //

        // 1. 랜덤 주식 가져오기
        StockInfoRes randomStock = stockClient.getRandomStock();
        log.info("랜덤 주식 선택 완료 - stockCode: {}, stockName: {}", randomStock.getCode(), randomStock.getName());

        // 2. 주식의 전일 종가 조회
        StockClosePriceRes price = userClient.getStockClosingPrice(randomStock.getCode());
        log.info("주식 전일 종가 조회 완료 - stockCode: {}, closingPrice: {}", randomStock.getCode(), price.getClosingPrice());

        // 3. 지급할 주식 양 계산
        BigDecimal amount = BigDecimal.valueOf(10000).divide(price.getClosingPrice(), 8, RoundingMode.FLOOR);
        log.info("사용자에게 지급할 주식 계산 완료 - userId: {}, stockCode: {}, amount: {}", userId, randomStock.getCode(), amount);

        // 4. 사용자 주식 업데이트
        userClient.updateUserStock(userId, randomStock.getCode(), amount, price.getClosingPrice(), "BUY");
        log.info("사용자 주식 업데이트 완료 - userId: {}, stockCode: {}, amount: {}", userId, randomStock.getCode(), amount);

        // 5. 알 삭제
        eggRepository.delete(egg);
        log.info("알 삭제 완료 - eggId: {}", eggId);

        return new EggHatchingRes(randomStock.getName(), amount, randomStock.getImg());

    }


}