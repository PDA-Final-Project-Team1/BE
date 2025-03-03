package com.team1.etuser.user.domain;

import com.team1.etcommon.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_stock")
public class UserStock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "stock_code", nullable = false, length = 6)
    private String stockCode;

    @Column(name = "amount", nullable = false)
    private int amount;

    @DecimalMin(value = "0.0")
    @Column(name = "average_price", nullable = false, precision = 65, scale = 3)
    private BigDecimal averagePrice;
}
