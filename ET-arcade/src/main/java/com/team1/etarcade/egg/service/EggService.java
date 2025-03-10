package com.team1.etarcade.egg.service;


import com.team1.etarcade.egg.connector.StockFeignConnector;
import com.team1.etarcade.egg.connector.UserFeignConnector;
import com.team1.etarcade.egg.domain.Egg;
import com.team1.etarcade.egg.dto.*;
import com.team1.etarcade.egg.repository.EggRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EggService {


    private final EggRepository eggRepository;
    private final UserFeignConnector userFeignConnector;
    private static final Duration INCUBATION_DURATION = Duration.ofMinutes(1); // 부화 시간 24시간
    private final StockFeignConnector stockFeignConnector;

    @Transactional
    //알 얻는 과정
    public EggCreateRes acquireEgg(Long userId) { //알얻기
        // FeignClient를 통해 사용자 정보 조회
        UserFeignPointRes userInfo = userFeignConnector.getUserPointInfo(userId);

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

    //유저의 알인지 확인
    private boolean isUserEgg(Long userId, Long eggId) {
        return eggRepository.existsByIdAndUserId(eggId, userId);
    }

    //알 부화 및 주식 지급 로직
    public void hatchEggAndRewardStock(Long userId, Long eggId) {


        if (!isUserEgg(userId, eggId)) {
            throw new IllegalStateException("해당 알은 사용자의 것이 아닙니다.");
        }

        //알이 존재하는지 확인
        Egg egg = eggRepository.findById(eggId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알을 찾을 수 없습니다."));

        if (!egg.isHatchable() || egg.isHatched()) {
            throw new IllegalStateException("부화할 수 없는 알입니다.");
        }

        //

        //1. feign으로, Stock 단에서 랜덤뽑기, 뽑힌주식 (전일종가)고려해서 주식 양  전달해주기.
        StockNameAndCodeDTO randomStock = stockFeignConnector.getRandomStock();
        StockPreviousCloseDto price  = userFeignConnector.getStockClosingPrice(randomStock.getCode());
        BigDecimal amount = BigDecimal.valueOf(10000).divide(price, 8, RoundingMode.FLOOR);


        //2.가져온 주식 및 양으로 유저에 추가하기.
        userFeignConnector.updateUserStock(userid,stockCode,amount,price,position);


        // 3.. 알 부화 처리 후 삭제

        eggRepository.delete(egg);

    }



}