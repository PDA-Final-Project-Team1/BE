package com.team1.etcore.chart;

import com.team1.etcore.chart.dto.StockResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class ChartDataController {
    private final ChartDataService chartDataService;

    @GetMapping("/{stockCode}")
    public ResponseEntity<?> getStock(@PathVariable String stockCode) {
        StockResponseDto stockResponseDto = chartDataService.getStockData(stockCode);
        return ResponseEntity.ok().body(stockResponseDto);
    }
}
