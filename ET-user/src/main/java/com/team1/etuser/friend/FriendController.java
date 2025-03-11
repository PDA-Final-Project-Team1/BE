package com.team1.etuser.friend;

import com.team1.etuser.friend.dto.SubscriptionRequestDto;
import com.team1.etuser.friend.dto.SubscriptionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/subscription")
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping
    public ResponseEntity<SubscriptionResponseDto> getSubscriptions(@RequestHeader(value = "X-Id") Long id) {
        SubscriptionResponseDto subscriptionResponseDto = friendService.getSubscriptions(id);
        return ResponseEntity.ok(subscriptionResponseDto);
    }


    @PostMapping
    public ResponseEntity<Void> subscribe(@RequestHeader(value = "X-Id") Long id, @RequestBody SubscriptionRequestDto requestDto) {
        friendService.subscribe(id, requestDto.getSubscribedId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(@RequestHeader(value = "X-Id") Long id, @RequestBody SubscriptionRequestDto requestDto) {
        friendService.unsubscribe(id, requestDto.getSubscribedId());
        return ResponseEntity.ok().build();

    }
}
