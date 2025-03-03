package com.team1.etuser.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPetResponseDto {
    private Long id;      // UserPet 엔티티의 id
    private Long petId;   // 펫의 id
}
