package com.team1.etuser.user.controller;

import com.team1.etuser.user.dto.SubscriptionRequestDto;
import com.team1.etuser.user.dto.SubscriptionResponseDto;
import com.team1.etuser.user.service.FriendService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/subscription/{userId}")
    public SubscriptionResponseDto getSubscriptions(@PathVariable Long userId) {
        return friendService.getSubscriptions(userId);
    }


    @PostMapping("/subscription")
    public void subscribe(@RequestBody SubscriptionRequestDto requestDto) {
        friendService.subscribe(requestDto);
    }

}
