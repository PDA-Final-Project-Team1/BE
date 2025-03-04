package com.team1.etcore.stock.controller;

import com.team1.etcore.stock.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RequestMapping("/sse")
@Controller
@CrossOrigin(origins = "http://localhost:5173")
public class SseController {
    private final SseService sseService;

    @GetMapping("/subscribe/trade-price")//관심종목 현재가
    public SseEmitter StockInterestPrice(@RequestHeader("X-Id") String userId) {
        System.out.println("컨트롤러 체크");

        return sseService.getInterestStockPrice(userId);
    }
    @GetMapping("/subscribe/ask-price")//보유 종목 현재가
    public SseEmitter StockPortfolioPrice(@RequestHeader("X-Id") String userId) {
        return sseService.getPortfolioStockPrice(userId);
    }
    @GetMapping("/ask-bid/{stockCode}")
    public SseEmitter StockAskBid(@PathVariable String stockCode) {
        return sseService.getAskBidPrice(stockCode);

    }

}
