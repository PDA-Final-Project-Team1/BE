package com.team1.etuser.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserAccountInfoRes {
    private BigDecimal deposit;
    private String account;

}
