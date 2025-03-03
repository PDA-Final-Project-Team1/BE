package com.team1.etarcade.pet.service;

import com.team1.etarcade.pet.domain.Pet;
import com.team1.etarcade.pet.dto.UserPetResponseDTO;
import com.team1.etarcade.pet.connector.UserFeignConnector;
import com.team1.etarcade.pet.repository.PetRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {

    private final UserFeignConnector userFeignConnector;
    private final PetRepository petRepository;

    public List<Pet> getUserPets(Long userId) {
        List<UserPetResponseDTO> userPets = userFeignConnector.getUserPets(userId);

        return userPets.stream()
                .map(userPet -> petRepository.findById(userPet.getPetId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
