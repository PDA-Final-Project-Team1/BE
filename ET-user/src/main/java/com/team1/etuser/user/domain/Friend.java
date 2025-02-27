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

    // subscriberId에 해당하는 User 엔티티 (구독하는 사람)
    @MapsId("subscriberId")
    @ManyToOne
    @JoinColumn(name = "subscriber_id", referencedColumnName = "id")
    private User subscriber;

    // subscribedId에 해당하는 User 엔티티 (구독 대상)
    @MapsId("subscribedId")
    @ManyToOne
    @JoinColumn(name = "subscribed_id", referencedColumnName = "id")
    private User subscribed;
}

