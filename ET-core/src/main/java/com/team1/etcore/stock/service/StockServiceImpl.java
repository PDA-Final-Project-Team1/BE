package com.team1.etcore.stock.service;

import com.team1.etcore.stock.domain.Stock;
import com.team1.etcore.stock.dto.StockInfoRes;
import com.team1.etcore.stock.dto.StockRes;
import com.team1.etcore.stock.repository.StockRepository;
import com.team1.etcore.stock.util.HangulUtils;
import com.team1.etcore.stock.util.Trie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
        Stock stock = stockRepository.getRandomStock();

        return StockInfoRes.builder()
                .code(stock.getStockCode())
                .name(stock.getName())
                .img(stock.getImg())
                .build();
    }
}
