package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.Friend;
import com.team1.etuser.user.dto.FriendResponseDto;
import com.team1.etuser.user.dto.SubscriptionResponseDto;
import com.team1.etuser.user.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final FriendRepository friendRepository;

    public UserService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    // 구독하는 사람 목록 조회 (subscriberId 기준)
    public SubscriptionResponseDto  getSubscriptions(Long userId) {
        List<FriendResponseDto> friends = friendRepository.findBySubscriber_Id(userId)
                .stream()
                .map(FriendResponseDto::new)
                .collect(Collectors.toList());

        return new SubscriptionResponseDto(friends.size(), friends);    }

}
