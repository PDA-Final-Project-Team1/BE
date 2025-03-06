package com.team1.etuser.user.dto.feign;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointRes {

    private int point;
    private boolean hasEnoughPoints;

}
