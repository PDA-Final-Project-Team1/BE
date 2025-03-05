package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.UserAdditionalInfo;
import com.team1.etuser.user.repository.UserAdditionalInfoRepository;
import com.team1.etuser.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdditionalService {
    private final UserAdditionalInfoRepository userAdditionalInfoRepository;
    private final UserRepository userRepository;

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

        UserAdditionalInfo userAdditionalInfo = userAdditionalInfoRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 추가정보를 찾을 수 없습니다."));

        BigDecimal result = userAdditionalInfo.getDeposit().add(amount);

        userAdditionalInfo.setDeposit(result);
        userAdditionalInfoRepository.save(userAdditionalInfo);

        return true;
    }
}
