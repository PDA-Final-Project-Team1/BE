package com.team1.etuser.user.domain;

import com.team1.etcommon.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_additional_info")
public class UserAdditionalInfo extends BaseEntity {
    @Id
    private Long user_id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @DecimalMin(value = "0.0")
    @Column(nullable = false, precision = 65, scale = 3)
    private BigDecimal deposit;

    @Setter
    @Column(nullable = false)
    private int point;

    @Column(nullable = false, length = 50)
    private String account;
}
