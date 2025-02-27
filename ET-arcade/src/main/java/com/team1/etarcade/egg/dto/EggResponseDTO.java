package com.team1.etarcade.egg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EggResponseDTO {
    private Long eggId;
    private Long userId;
    private boolean is_hatchable;
    private boolean is_hatched;
    private LocalDateTime createdAt;
    private String timeRemaining;
}