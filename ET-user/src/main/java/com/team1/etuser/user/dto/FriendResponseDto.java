package com.team1.etuser.user.dto;

import com.team1.etuser.user.domain.Friend;
import lombok.Getter;

@Getter
public class FriendResponseDto {
    private final Long id;
    private final String name;

    public FriendResponseDto(Friend friend) {
        this.id = friend.getSubscribed().getId(); // 구독 대상자의 id
        this.name = friend.getSubscribed().getName(); // 구독 대상자의 이름
    }
}
