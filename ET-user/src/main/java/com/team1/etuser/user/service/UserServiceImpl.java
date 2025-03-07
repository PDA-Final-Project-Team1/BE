package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.dto.StockDTO;
import com.team1.etuser.user.dto.UserAccountInfoRes;
import com.team1.etuser.user.dto.UserHistoryRes;
import com.team1.etuser.user.dto.UserInfoRes;
import com.team1.etuser.user.dto.UserResponseDto;
import com.team1.etuser.user.dto.UserStocksRes;
import com.team1.etuser.user.repository.FriendRepository;
import com.team1.etuser.user.repository.UserAdditionalInfoRepository;
import com.team1.etuser.user.repository.UserRepository;
import com.team1.etuser.user.repository.UserStockRepository;
import com.team1.etuser.user.repository.UserTradeHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserAdditionalInfoRepository userAdditionalInfoRepository;
    private final UserTradeHistoryRepository userTradeHistoryRepository;
    private final StockFeignService stockFeignService;
    private final UserStockRepository userStockRepository;
    private final FriendRepository friendRepository;

    /**
     * @param uid 유저의 로그인 id
     * @return 존재할 시 true
     */
    @Override
    public boolean isDuplicate(String uid) {
        return userRepository.existsByUid(uid);
    }

    /**
     * @return 로그인한 User의 uid, name
     */
    @Override
    public UserInfoRes getUserInfo(String userId) {
//        Long id = 1L; // Token에 존재하는 id로 변경 예정
        Long id = Long.valueOf(userId);

        User user = userRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        return UserInfoRes.builder()
                .uid(user.getUid())
                .name(user.getName())
                .build();
    }

    /**
     * @return 로그인 User의 account, deposit
     */
    @Override
    public UserAccountInfoRes getUserAccountInfo(String userId) {
//        Long id = 1L; // JWT토큰에서 추출한 값으로 변경 예정
        Long id = Long.valueOf(userId);

        return userAdditionalInfoRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("추가정보가 존재하지 않습니다."));
    }

    /**
     * @return 로그인 User의 거래내역 조회
     */
    @Override
    public List<UserHistoryRes> getUserHistory(String userId) {
        Long id = Long.valueOf(userId);

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("사용자를 찾지 못했습니다."));

        List<UserHistoryRes> userHistoryResList = userTradeHistoryRepository.findUserHistory(user);

        log.info(userHistoryResList.toString());

        for (UserHistoryRes u : userHistoryResList) {
            StockDTO stockDTO = stockFeignService.getStock(u.getStockCode());
            if (stockDTO != null && stockDTO.getName() != null) {
                u.setStockName(stockDTO.getName());
            } else {
                // stockName을 기본값 또는 알림 메시지를 설정
                u.setStockName("Unknown Stock");
                log.warn("Stock name not found for stockCode: {}", u.getStockCode());
            }
        }

        return userHistoryResList;
    }

    /**
     * @return 로그인 User의 보유 주식 조회
     */
    @Override
    public List<UserStocksRes> getUserStocks(String userId) {
//        Long id = 1L;
        Long id = Long.valueOf(userId);

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("사용자를 찾지 못했습니다."));

        List<UserStocksRes> userStocksResList = userStockRepository.findByUserStocks(user);

        if (userStocksResList.isEmpty()) {
            return null;
        }

        for (UserStocksRes u : userStocksResList) {
            String stockName = stockFeignService.getStock(u.getStockCode()).getName();
            String stockImage = stockFeignService.getStock(u.getStockCode()).getImg();
            u.setStockName(stockName);
            u.setStockImage(stockImage);
        }

        return userStocksResList;
    }

    /**
    * @return User의 응답 값 반환
    */
    @Override
    public UserResponseDto getUserByUid(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found with uid: " + uid));
        boolean isSubscribed = friendRepository.existsBySubscriberId(user.getId());
        return new UserResponseDto(user.getId(), user.getUid(), user.getName(), isSubscribed);
    }
}
