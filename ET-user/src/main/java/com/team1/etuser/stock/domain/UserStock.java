package com.team1.etuser.stock.domain;

import com.team1.etcommon.domain.BaseEntity;
import com.team1.etuser.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

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

    @Setter
    @Column(name = "amount", nullable = false, precision = 65, scale = 8)
    private BigDecimal amount;

    @Setter
    @DecimalMin(value = "0.0")
    @Column(name = "average_price", nullable = false, precision = 65, scale = 3)
    private BigDecimal averagePrice;
}
