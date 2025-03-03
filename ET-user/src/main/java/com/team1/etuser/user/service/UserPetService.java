package com.team1.etuser.user.service;

import com.team1.etuser.user.domain.UserPet;
import com.team1.etuser.user.dto.UserPetResponseDto;
import com.team1.etuser.user.repository.UserPetRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPetService {
    private final UserPetRepository userPetRepository;

    public List<UserPetResponseDto> getUserPets(Long userId) {
        List<UserPet> userPets = userPetRepository.findByUser_Id(userId);
        return userPets.stream()
                .map(userPet -> new UserPetResponseDto(userPet.getId(), userPet.getPetId()))
                .collect(Collectors.toList());
    }
}


