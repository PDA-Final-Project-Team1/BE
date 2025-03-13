package com.team1.etcore.stock.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etcore.stock.domain.Stock;
import com.team1.etcore.stock.dto.StockDataRes;
import com.team1.etcore.stock.dto.StockInfoRes;
import com.team1.etcore.stock.dto.StockRes;
import com.team1.etcore.stock.repository.StockRepository;
import com.team1.etcore.stock.util.HangulUtils;
import com.team1.etcore.stock.util.Trie;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StockCache stockCache;
    private Trie trie;

    @Override
    public Stock getStock(String stockCode) {
        return stockRepository.findByStockCode(stockCode);
    }

    @Override
    public Set<StockRes> searchStocks(String keyword) {
        return trie.search(keyword);
    }

    /**
     * Trie 초기화: 외부에서 캐싱된 주식 목록을 전달받아 Trie를 구성.
     */
    public void initTrie(List<StockRes> stocks) {
        trie = new Trie();
        for (StockRes stock : stocks) {
            // 주식명 전체 인덱싱
            trie.insert(stock.getName(), stock);
            // 주식명의 초성 인덱싱 (예: "삼성전자" -> "ㅅㅅㅈㄴ")
            String initials = HangulUtils.getInitials(stock.getName());
            trie.insert(initials, stock);
        }
    }

    /**
     * 캐싱된 Stock 데이터를 StockResponseDTO 형식으로 변환하여 반환.
     */
    @Cacheable("stocks")
    public List<StockRes> getStockListAsDTO() {
        return stockCache.getStockList().stream()
                .map(stockData -> new StockRes(
                        stockData.getCode(),
                        stockData.getName(),
                        "https://static.toss.im/png-icons/securities/icn-sec-fill-" + stockData.getCode() + ".png"
                ))
                .collect(Collectors.toList());
    }





    @Override
    public StockInfoRes getRandomStock() {
        ObjectMapper objectMapper = new ObjectMapper();
        Random random = new Random();

        try {
            // StockData.json 파일을 읽어 리스트로 변환
            List<StockDataRes> stockList = objectMapper.readValue(
                    new ClassPathResource("stock/StockData.json").getInputStream(),
                    new TypeReference<List<StockDataRes>>() {}
            );

            // stockList가 비어있는지 확인
            if (stockList.isEmpty()) {
                throw new IllegalStateException("주식 데이터가 없습니다.");
            }

            // 랜덤한 주식 선택 후 반환
            StockDataRes selectedStock = stockList.get(random.nextInt(stockList.size()));
            return new StockInfoRes(
                    selectedStock.getCode(),
                    selectedStock.getName()

            );
        } catch (IOException e) {
            throw new RuntimeException("StockData.json 파일을 읽는 중 오류 발생", e);
        }

    }
}
