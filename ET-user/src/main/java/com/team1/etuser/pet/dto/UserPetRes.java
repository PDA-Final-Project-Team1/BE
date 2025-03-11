package com.team1.etuser.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPetRes {
    private Long id;      // UserPet 엔티티의 id
    private Long petId;   // 펫의 id
}
