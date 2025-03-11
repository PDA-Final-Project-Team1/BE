package com.team1.etuser.user.domain;

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
@Table(indexes = {
        @Index(name = "idx_uid", columnList = "uid")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 25)
    private String uid;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false, length = 10)
    private String name;
}
