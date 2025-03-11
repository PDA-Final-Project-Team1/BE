package com.team1.etuser.friend.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class SubscriptionRes {
    private final int counts;
    private final List<FriendRes> friends;

    public SubscriptionRes(int counts, List<FriendRes> friends) {
        this.counts = counts;
        this.friends = friends;
    }
}
