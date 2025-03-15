package com.team1.etcore.stock.controller;

import com.team1.etcore.stock.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/sse")
@Controller
public class SseController {
    private final SseService sseService;

//    @GetMapping("/subscribe/interest-price")//관심종목 현재가
//    public SseEmitter StockInterestPrice(@RequestHeader("X-Id") String userId) {
//        return sseService.getInterestStockPrice(userId);
//    }
//    @GetMapping("/subscribe/portfolio-price")//보유 종목 현재가
//    public SseEmitter StockPortfolioPrice(@RequestHeader("X-Id") String userId) {
//        return sseService.getPortfolioStockPrice(userId);
//    }
//    @GetMapping("/ask-bid/{stockCode}")
//    public SseEmitter StockAskBid(@PathVariable String stockCode) {
//        return sseService.getAskBidPrice(stockCode);
//    }
//    @GetMapping("/cur-price/{stockCode}")
//    public SseEmitter StockCurPrice(@PathVariable String stockCode) {
//        return sseService.getStockCurPrice(stockCode);
//    }

    @GetMapping("/subscribe")
    public SseEmitter subscribeUser(@RequestHeader("X-Id") String userId,
                                    @RequestParam(required = false) String stockCodes) {
        List<String> codeList = null;
        if (stockCodes != null && !stockCodes.isEmpty()) {
            codeList = Arrays.asList(stockCodes.split(","));
        }
        return sseService.subscribeUser(userId, codeList);
    }

    @GetMapping(value = "/subscribe/trade", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeTrade(@RequestHeader("X-Id") Long userId) {
        return sseService.subscribeTradeNotifications(userId);
    }
}
