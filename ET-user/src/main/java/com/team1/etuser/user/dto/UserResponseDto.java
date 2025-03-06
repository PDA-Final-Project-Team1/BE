package com.team1.etuser.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String uid;
    private String name;
    private boolean subscribed;
}