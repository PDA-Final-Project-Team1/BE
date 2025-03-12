package com.team1.etuser.user.service;

import com.team1.etuser.stock.domain.TradeStatus;
import com.team1.etuser.stock.service.StockFeignService;
import com.team1.etuser.user.domain.User;
import com.team1.etuser.stock.dto.StockRes;
import com.team1.etuser.user.dto.UserAccountInfoRes;
import com.team1.etuser.stock.dto.UserHistoryRes;
import com.team1.etuser.user.dto.UserInfoRes;
import com.team1.etuser.user.dto.UserSearchRes;
import com.team1.etuser.stock.dto.UserStocksRes;
import com.team1.etuser.friend.FriendRepository;
import com.team1.etuser.user.repository.UserAdditionalInfoRepository;
import com.team1.etuser.user.repository.UserRepository;
import com.team1.etuser.stock.repository.UserStockRepository;
import com.team1.etuser.stock.repository.UserTradeHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<UserHistoryRes> getUserHistory(String userId, Pageable pageable, TradeStatus tradeStatus) {
        Long id = Long.valueOf(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾지 못했습니다."));

        Page<UserHistoryRes> historyPage = userTradeHistoryRepository.findUserHistoryByTradeStatus(user, tradeStatus, pageable);

        // 각 내역에 대해 주식 이름 및 이미지를 업데이트
        historyPage.forEach(u -> {
            StockRes stockRes = stockFeignService.getStock(u.getStockCode());
            if (stockRes != null && stockRes.getName() != null) {
                u.setStockName(stockRes.getName());
                u.setImg(stockRes.getImg());
            } else {
                u.setStockName("Unknown Stock");
                log.warn("Stock name not found for stockCode: {}", u.getStockCode());
            }
        });

        return historyPage;
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
    public UserSearchRes getUserByUid(Long userId, String uid) {
        // 로그인한 사용자 정보 가져오기
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("현재 로그인한 사용자를 찾을 수 없습니다."));

        // 본인의 uid 조회 차단
        if (currentUser.getUid().equals(uid)) {
            throw new RuntimeException("본인 계정은 검색할 수 없습니다.");
        }

        // 검색 대상 사용자 찾기
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found with uid: " + uid));

        boolean isSubscribed = friendRepository.existsBySubscriberId(user.getId());

        return new UserSearchRes(user.getId(), user.getUid(), user.getName(), isSubscribed);
    }
}
