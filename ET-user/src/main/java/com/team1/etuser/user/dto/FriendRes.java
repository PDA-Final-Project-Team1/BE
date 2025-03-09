package com.team1.etuser.user.dto;

import com.team1.etuser.user.domain.Friend;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRes {
    private Long id;
    private String name;
    private String uid;

    public FriendRes(Friend friend) {
        this.id = friend.getSubscribed().getId();
        this.name = friend.getSubscribed().getName();
        this.uid = friend.getSubscribed().getUid();
    }
}
