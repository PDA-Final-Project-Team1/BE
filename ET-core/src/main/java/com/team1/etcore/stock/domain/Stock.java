package com.team1.etcore.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @Column(name = "stock_code", nullable = false, length = 6)
    private String stockCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 10)
    private String market;

    @Column(nullable = false)
    private String img;

}
