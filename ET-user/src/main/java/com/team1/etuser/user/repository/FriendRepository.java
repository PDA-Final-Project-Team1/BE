package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.Friend;
import com.team1.etuser.user.domain.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, FriendId> {
    // 구독 목록 조회
    List<Friend> findBySubscriber_Id(Long subscriberId);
}
