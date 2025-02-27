package com.team1.etarcade.egg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EggResponseDTO {
    private Long eggId;
    private Long userId;
    private String hatchableStatus;
    private String hatchedStatus;
    private LocalDateTime createdAt;
    private String timeRemaining;
}