package com.team1.etarcade.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PetGrantRes {
    private Long petId;
    private String img;
    private String name;
}
