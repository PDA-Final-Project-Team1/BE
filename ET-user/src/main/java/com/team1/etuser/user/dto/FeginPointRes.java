package com.team1.etuser.user.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeginPointRes {

    private int point;
    private boolean hasEnoughPoints;

}
