package com.team1.etarcade.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDTO {
    private int count;
    private List<UserFriendResponseDTO> friends;
}
