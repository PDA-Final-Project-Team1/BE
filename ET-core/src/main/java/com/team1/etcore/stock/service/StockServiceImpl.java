package com.team1.etcore.stock.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.etcore.stock.domain.Stock;
import com.team1.etcore.stock.dto.StockResponseDTO;
import com.team1.etcore.stock.repository.StockRepository;
import com.team1.etcore.stock.util.HangulUtils;
import com.team1.etcore.stock.util.StockData;
import com.team1.etcore.stock.util.Trie;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService{
    private final StockRepository stockRepository;

    private Trie trie;

    @PostConstruct
    public void init() {
        trie = new Trie();
        List<StockResponseDTO> stocks = loadStocks();
        for (StockResponseDTO stock : stocks) {
            // 주식명 전체를 인덱싱
            trie.insert(stock.getName(), stock);
            // 주식명의 초성을 인덱싱 (예: "삼성전자" -> "ㅅㅅㅈㄴ")
            String initials = HangulUtils.getInitials(stock.getName());
            trie.insert(initials, stock);
        }
    }


    @Override
    public Stock getStock(String stockCode) {
        return stockRepository.findByStockCode(stockCode);
    }

    @Override
    // 검색 메서드: 키워드가 주어지면 Trie에서 검색하여 결과를 반환
    public Set<StockResponseDTO> searchStocks(String keyword) {
        return trie.search(keyword);
    }


    // 임시로 초기 주식 목록 생성. 실제 개발에서는 DB나 외부 API에서 데이터를 가져올 수 있습니다.
    private List<StockResponseDTO> loadStocks() {
        List<StockResponseDTO> stocks = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        // 클래스패스에서 파일 읽기
        try (InputStream inputStream = getClass().getResourceAsStream("/stock/StockData.json")) {
            if (inputStream == null) {
                throw new FileNotFoundException("StockData.json 을 경로에서 찾지 못했습니다.");
            }

            List<StockData> stockDataList = objectMapper.readValue(inputStream, new TypeReference<>() {});
            for (StockData stockData : stockDataList) {
                String imageUrl = "https://static.toss.im/png-icons/securities/icn-sec-fill-" + stockData.getCode() + ".png";
                stocks.add(new StockResponseDTO(stockData.getCode(), stockData.getName(), imageUrl));
            }
        } catch (IOException e) {
            throw new RuntimeException("Stock 데이터를 로드하는 중 문제가 발생했습니다.", e);
        }
        return stocks;
    }

}
