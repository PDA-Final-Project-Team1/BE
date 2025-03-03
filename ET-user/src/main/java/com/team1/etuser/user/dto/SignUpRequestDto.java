package com.team1.etuser.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    private String uid;
    private String pwd;
    private String name;
}
