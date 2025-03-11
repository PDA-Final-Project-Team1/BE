package com.team1.etuser.friend.dto;

import com.team1.etuser.friend.dto.FriendRes;
import lombok.Getter;
import java.util.List;

@Getter
public class SubscriptionResponseDto {
    private final int counts;
    private final List<FriendRes> friends;

    public SubscriptionResponseDto(int counts, List<FriendRes> friends) {
        this.counts = counts;
        this.friends = friends;
    }
}
