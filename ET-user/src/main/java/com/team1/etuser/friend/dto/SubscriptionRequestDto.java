package com.team1.etuser.friend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubscriptionRequestDto {
    private Long subscribedId;  // 구독 대상

    public SubscriptionRequestDto(Long subscribedId) {
        this.subscribedId = subscribedId;
    }
}
