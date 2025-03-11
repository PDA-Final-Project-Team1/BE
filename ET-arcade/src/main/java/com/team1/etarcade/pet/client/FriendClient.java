package com.team1.etarcade.pet.client;

import com.team1.etarcade.pet.dto.SubscriptionRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ET-user", path = "/api/users/subscription", contextId = "friendFeignConnector")
public interface FriendClient {
    @GetMapping
    SubscriptionRes getSubscriptions(@RequestHeader("X-Id") Long subscriberId);
}
