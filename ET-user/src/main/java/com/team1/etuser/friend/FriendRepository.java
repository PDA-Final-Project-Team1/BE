package com.team1.etuser.friend;

import com.team1.etuser.friend.domain.Friend;
import com.team1.etuser.friend.domain.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, FriendId> {
    // 구독 목록 조회
    List<Friend> findBySubscriberId(Long subscriberId);
    boolean existsBySubscriberId(Long subscriberId);
}
