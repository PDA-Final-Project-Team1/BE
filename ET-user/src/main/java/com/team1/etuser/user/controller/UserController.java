package com.team1.etuser.user.controller;

import com.team1.etuser.user.domain.Friend;
import com.team1.etuser.user.dto.SubscriptionResponseDto;
import com.team1.etuser.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/subscription/{userId}")
    public SubscriptionResponseDto getSubscriptions(@PathVariable Long userId) {
        return userService.getSubscriptions(userId);
    }



}