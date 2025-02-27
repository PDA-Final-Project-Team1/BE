package com.team1.etuser.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubscriptionRequestDto {
    private Long subscriberId;  // 구독하는 사람
    private Long subscribedId;  // 구독 대상

    public SubscriptionRequestDto(Long subscriberId, Long subscribedId) {
        this.subscriberId = subscriberId;
        this.subscribedId = subscribedId;
    }
}
