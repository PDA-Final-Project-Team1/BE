package com.team1.etarcade.egg.domain;

import com.team1.etcommon.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Egg extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Setter
    @Column(name = "is_hatchable", nullable = false)
    private boolean isHatchable;

    @Setter
    @Column(name = "is_hatched", nullable = false)
    private boolean isHatched;
}
