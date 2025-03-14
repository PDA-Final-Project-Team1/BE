package com.team1.etuser.stock.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.team1.etuser.stock.domain.Position;
import com.team1.etuser.stock.repository.UserFavoriteStockRepository;
import com.team1.etuser.user.domain.User;
import com.team1.etuser.stock.domain.UserStock;
import com.team1.etuser.stock.dto.StockClosePriceRes;
import com.team1.etuser.user.repository.UserRepository;
import com.team1.etuser.stock.repository.UserStockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserStockService {
    private final UserStockRepository userStockRepository;
    private final UserFavoriteStockRepository userFavoriteStockRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate;
    private static final long REDIS_TTL_HOURS = 6;

    public boolean updateUserStock(Long userId, String stockCode, BigDecimal amount, BigDecimal price, Position position) {
        // 유저 조회
        Optional<User> user = userRepository.findById(userId);
        log.info("사용자 {}",user.get().getId());
        if (user.isEmpty()) {
            return false;
        }
        // 유저 보유 주식이 존재하는지 조회
        UserStock userStock = userStockRepository.findByUserAndStockCode(user.get(), stockCode);
        log.info("유저스톡 {}",userStock);
        if (position.equals(Position.BUY)) {
            if (userStock == null) {
                // 보유 주식이 존재하지 않으면 새로운 Row 생성
                userStock = UserStock.builder()
                        .amount(amount)
                        .averagePrice(price)
                        .stockCode(stockCode)
                        .user(user.get())
                        .build();
            } else {
                // 기존 보유 수량과 평단가를 기준으로 새로운 평단가 계산
                BigDecimal existingAmount = userStock.getAmount();
                BigDecimal existingAveragePrice = userStock.getAveragePrice();

                BigDecimal newAmount = existingAmount.add(amount);
                // (기존 평단가 * 기존 개수) + (새로운 가격 + 새로운 개수)
                BigDecimal totalCost = existingAveragePrice.multiply(existingAmount)
                        .add(price.multiply(amount));

                // 새로운 평단가
                BigDecimal newAveragePrice = totalCost.divide(newAmount, MathContext.DECIMAL128);

                // setter를 사용하여 amount와 averagePrice만 수정
                userStock.setAmount(newAmount);
                userStock.setAveragePrice(newAveragePrice);
            }
            userStockRepository.save(userStock);
            return true;
        } else if (position.equals(Position.SELL)) {
            if (userStock == null) {
                return false;
            }

            BigDecimal existingAmount = userStock.getAmount();
            if (existingAmount.compareTo(amount) < 0) {
                return false;
            }

            BigDecimal newAmount = existingAmount.subtract(amount);
            if (newAmount.compareTo(BigDecimal.ZERO) == 0) { // newAmount == 0
                // 매도 후 수량이 0이면 해당 row 삭제
                userStockRepository.delete(userStock);
            } else {
                // 매도 후 남은 수량 업데이트 (평단가는 그대로 유지)
                userStock.setAmount(newAmount);
                userStockRepository.save(userStock);
            }
            return true;
        } else {
            return false;
        }
    }

    // 보유 주식의 전일 종가 가져오기
    public List<StockClosePriceRes> getUserStockClosingPrice(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        List<StockClosePriceRes> stockList = userStockRepository.findStockCodeByUser(user);

        for (StockClosePriceRes stock : stockList) {
            String redisKey = CloseKey(stock.getStockCode());
            String closingPriceStr = redisTemplate.opsForValue().get(redisKey);

            if (closingPriceStr == null) {
                closingPriceStr = fetchClosingPriceFromApi(stock.getStockCode());
                if (closingPriceStr != null) {
                    redisTemplate.opsForValue().set(redisKey, closingPriceStr, REDIS_TTL_HOURS, TimeUnit.HOURS);
                }
            }

            BigDecimal closingPrice = parsePrice(closingPriceStr);
            stock.setClosingPrice(closingPrice);
        }
        return stockList;
    }

    //관심주식의 전일 종가 가져오기
    public List<StockClosePriceRes> getUserFavoriteStockClosingPrice(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        //List<StockClosePriceRes> stockList = userStockRepository.findStockCodeByUser(user);
        List<StockClosePriceRes> stockList = userFavoriteStockRepository.findFavoriteStockCodeByUser(user);

        for (StockClosePriceRes stock : stockList) {
            String redisKey = CloseKey(stock.getStockCode());
            String closingPriceStr = redisTemplate.opsForValue().get(redisKey);

            if (closingPriceStr == null) {
                closingPriceStr = fetchClosingPriceFromApi(stock.getStockCode());
                if (closingPriceStr != null) {
                    redisTemplate.opsForValue().set(redisKey, closingPriceStr, REDIS_TTL_HOURS, TimeUnit.HOURS);
                }
            }

            BigDecimal closingPrice = parsePrice(closingPriceStr);
            stock.setClosingPrice(closingPrice);
        }
        return stockList;
    }


    // 특정 종목의 전일 종가 가져오기
    public StockClosePriceRes getStockClosingPrice(String stockCode) {
        String redisKey = CloseKey(stockCode);
        String closingPriceStr = redisTemplate.opsForValue().get(redisKey);

        if (closingPriceStr == null) {
            closingPriceStr = fetchClosingPriceFromApi(stockCode);
            if (closingPriceStr != null) {
                redisTemplate.opsForValue().set(redisKey, closingPriceStr, REDIS_TTL_HOURS, TimeUnit.HOURS);
            }
        }

        BigDecimal closingPrice = parsePrice(closingPriceStr);

        return StockClosePriceRes.builder()
                .stockCode(stockCode)
                .closingPrice(closingPrice)
                .build();
    }

    // 전일 종가를 담을 redis key
    private String CloseKey(String stockCode) {
        return "close:"+stockCode;
    }

    // 전일 종가를 네이버 크롤링을 통해 조회
    private String fetchClosingPriceFromApi(String stockCode) {
        String url = "https://m.stock.naver.com/api/stock/" + stockCode + "/integration";
        JsonNode root = restTemplate.getForObject(url, JsonNode.class);

        if (root != null && root.has("totalInfos")) {
            for (JsonNode node : root.get("totalInfos")) {
                if ("전일".equals(node.get("key").asText())) {
                    return node.get("value").asText();
                }
            }
        }
        return null;
    }

    // 전일 종가가 57,000과 같이 들어오는 것을 파싱
    private BigDecimal parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return null;
        }
        try {
            String normalized = priceStr.replaceAll(",", "");
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            // 필요한 경우 로그 처리
            return null;
        }
    }
}
