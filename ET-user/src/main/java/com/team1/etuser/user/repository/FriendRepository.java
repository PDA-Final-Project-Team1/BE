package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.Friend;
import com.team1.etuser.user.domain.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, FriendId> {
}
