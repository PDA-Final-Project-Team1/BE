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
    private Long user_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "stock_code", nullable = false, length = 6)
    private String stockCode;

    @DecimalMin(value = "0.0")
    @Column(nullable = false, precision = 65, scale = 6)
    private BigDecimal amount;

    @DecimalMin(value = "0.0")
    @Column(name = "average price", nullable = false, precision = 65, scale = 3)
    private BigDecimal averagePrice;
}
