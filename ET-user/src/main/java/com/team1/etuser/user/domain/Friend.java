package com.team1.etuser.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Friend {

    @EmbeddedId
    private FriendId friendId;

    // user_id1에 대응하는 User 엔티티
    @MapsId("userId1")
    @ManyToOne
    @JoinColumn(name = "user_id1", referencedColumnName = "id")
    private User user1;

    // user_id2에 대응하는 User 엔티티
    @MapsId("userId2")
    @ManyToOne
    @JoinColumn(name = "user_id2", referencedColumnName = "id")
    private User user2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;
}
