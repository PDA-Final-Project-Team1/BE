package com.team1.etcore.stock.initializer;

import com.team1.etcore.stock.service.StockServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockInitializer {
    private final StockServiceImpl stockServiceImpl;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // 외부에서 getStockListAsDTO()를 호출하므로 프록시가 개입되어 @Cacheable이 정상 동작.
        stockServiceImpl.initTrie(stockServiceImpl.getStockListAsDTO());
    }
}
