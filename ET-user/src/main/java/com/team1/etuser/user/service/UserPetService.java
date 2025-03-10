package com.team1.etuser.user.service;

import com.team1.etuser.user.connector.PetFeignConnector;
import com.team1.etuser.user.domain.Pet;
import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserPet;
import com.team1.etuser.user.dto.PetGrantRequestDto;
import com.team1.etuser.user.dto.UserPetResponseDto;
import com.team1.etuser.user.dto.UserUniquePetsRes;
import com.team1.etuser.user.repository.UserPetRepository;
import com.team1.etuser.user.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPetService {
    private final UserPetRepository userPetRepository;
    private final UserRepository userRepository;
    private final PetFeignConnector petFeignConnector;

    public List<UserPetResponseDto> getUserPets(Long userId) {
        List<UserPet> userPets = userPetRepository.findByUserId(userId);
        return userPets.stream()
                .map(userPet -> new UserPetResponseDto(userPet.getId(), userPet.getPetId()))
                .collect(Collectors.toList());
    }

    public UserPet grantPet(Long userId, PetGrantRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserPet userPet = UserPet.builder()
                .user(user)
                .petId(requestDto.getPetId())
                .build();
        userPetRepository.save(userPet);

        return userPet;
    }

    public List<UserUniquePetsRes> getUniquePetsByUser(Long userId) {
        List<UserPet> userPets = userPetRepository.findByUserId(userId);

        // 중복 제거 (Set 사용)
        Set<Long> uniquePetIds = new HashSet<>();
        return userPets.stream()
                .filter(userPet -> uniquePetIds.add(userPet.getPetId())) // 중복 제거
                .map(userPet -> {
                    Pet pet = petFeignConnector.getPetById(userPet.getPetId()); // ET-Arcade에서 pet 정보 가져오기
                    return new UserUniquePetsRes(userPet.getPetId(), pet.getImg()); // Pet 이미지 포함
                })
                .collect(Collectors.toList());
    }

}


