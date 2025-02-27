package com.team1.etarcade.egg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EggResponseDTO {
    private Long eggId;
    private Long userId;
    private int remainingPoints;
    private String message;
}