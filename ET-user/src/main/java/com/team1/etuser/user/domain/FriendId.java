package com.team1.etuser.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FriendId implements Serializable {
    @Column(name = "subscriber_id", nullable = false) // 구독하는 사람
    private Long subscriberId;

    @Column(name = "subscribed_id", nullable = false) // 구독 대상
    private Long subscribedId;
}
