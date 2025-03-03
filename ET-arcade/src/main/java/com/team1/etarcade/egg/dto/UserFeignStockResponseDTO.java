package com.team1.etarcade.egg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


// 유저에게 지급할 소수점주식입니다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserFeignStockResponseDTO {
    private Long userId;
    private String randomstock;
    private Double quantity;


}