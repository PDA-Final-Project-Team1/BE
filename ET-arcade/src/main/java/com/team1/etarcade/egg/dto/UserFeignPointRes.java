package com.team1.etarcade.egg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserFeignPointRes {

    private int point;
    private Boolean hasEnoughPoints;


}