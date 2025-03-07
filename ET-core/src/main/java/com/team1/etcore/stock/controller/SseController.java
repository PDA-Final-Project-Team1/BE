package com.team1.etcore.stock.controller;

import com.team1.etcore.stock.service.SseService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RequestMapping("/sse")
@Controller
@CrossOrigin(origins = "http://localhost:5173")
public class SseController {
    private final SseService sseService;

    @GetMapping("/subscribe/interest-price")//관심종목 현재가
    public SseEmitter StockInterestPrice(@RequestHeader("X-Id") String userId) {
        return sseService.getInterestStockPrice(userId);
    }
    @GetMapping("/subscribe/portfolio-price")//보유 종목 현재가
    public SseEmitter StockPortfolioPrice(@RequestHeader("X-Id") String userId) {
        return sseService.getPortfolioStockPrice(userId);
    }
    @GetMapping("/ask-bid/{stockCode}")
    public SseEmitter StockAskBid(@PathVariable String stockCode) {
        return sseService.getAskBidPrice(stockCode);
    }

    @GetMapping(value = "/subscribe/trade", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeTrade(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Content-Type", "text/event-stream");

        return sseService.subscribeTradeNotifications(1L);
    }
}
