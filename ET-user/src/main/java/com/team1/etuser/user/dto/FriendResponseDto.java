package com.team1.etuser.user.dto;

import com.team1.etuser.user.domain.Friend;
import lombok.Getter;

@Getter
public class FriendResponseDto {
    private final String uId;
    private final String name;

    public FriendResponseDto(Friend friend) {
        this.uId = friend.getSubscribed().getUid(); // 구독 대상자의 uid
        this.name = friend.getSubscribed().getName(); // 구독 대상자의 이름
    }
}
