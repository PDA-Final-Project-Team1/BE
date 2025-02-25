package com.team1.etuser.user.domain;

import com.team1.etcommon.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_favorite_stock")
public class UserFavoriteStock extends BaseEntity {

    @Id
    private Long user_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "stock_code", nullable = false, length = 6)
    private String stockCode;
}
