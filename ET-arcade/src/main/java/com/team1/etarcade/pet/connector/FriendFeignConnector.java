package com.team1.etarcade.pet.connector;

import com.team1.etarcade.pet.dto.SubscriptionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ET-user", path = "/api/users/subscription", contextId = "friendFeignConnector")
public interface FriendFeignConnector {
    @GetMapping
    SubscriptionResponseDTO getSubscriptions(@RequestHeader("X-Id") Long subscriberId);
}
