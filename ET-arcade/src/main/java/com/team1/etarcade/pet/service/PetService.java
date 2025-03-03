package com.team1.etarcade.pet.service;

import com.team1.etarcade.pet.domain.Pet;
import com.team1.etarcade.pet.dto.PetGrantRequestDTO;
import com.team1.etarcade.pet.dto.UserPetResponseDTO;
import com.team1.etarcade.pet.connector.UserFeignConnector;
import com.team1.etarcade.pet.repository.PetRepository;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {

    private final UserFeignConnector userFeignConnector;
    private final PetRepository petRepository;

    // 사용자의 보유 펫 목록 조회 (ET-user 모듈에서 조회)
    public List<Pet> getUserPets(Long userId) {
        List<UserPetResponseDTO> userPets = userFeignConnector.getUserPets(userId);

        return userPets.stream()
                .map(userPet -> petRepository.findById(userPet.getPetId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 서버에서 랜덤하게 펫 지급 로직 (기존 지급 여부 검사는 하지 않음)
    public void grantRandomPet(Long userId) {
        // 전체 펫 목록 조회
        List<Pet> allPets = petRepository.findAll();
        if (allPets.isEmpty()) {
            throw new RuntimeException("No available pets to grant.");
        }
        // 무작위 펫 선택
        int randomIndex = ThreadLocalRandom.current().nextInt(allPets.size());
        Pet selectedPet = allPets.get(randomIndex);
        // 선택된 펫의 ID를 포함하는 요청 DTO 생성 후 ET-user 모듈에 지급 API 호출
        PetGrantRequestDTO requestDto = new PetGrantRequestDTO(selectedPet.getId());
        userFeignConnector.grantPet(userId, requestDto);
    }
}
