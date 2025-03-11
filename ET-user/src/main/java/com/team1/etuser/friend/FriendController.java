package com.team1.etuser.friend;

import com.team1.etuser.friend.dto.SubscriptionReq;
import com.team1.etuser.friend.dto.SubscriptionRes;
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
    public ResponseEntity<SubscriptionRes> getSubscriptions(@RequestHeader(value = "X-Id") Long id) {
        SubscriptionRes subscriptionRes = friendService.getSubscriptions(id);
        return ResponseEntity.ok(subscriptionRes);
    }


    @PostMapping
    public ResponseEntity<Void> subscribe(@RequestHeader(value = "X-Id") Long id, @RequestBody SubscriptionReq requestDto) {
        friendService.subscribe(id, requestDto.getSubscribedId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(@RequestHeader(value = "X-Id") Long id, @RequestBody SubscriptionReq requestDto) {
        friendService.unsubscribe(id, requestDto.getSubscribedId());
        return ResponseEntity.ok().build();

    }
}
