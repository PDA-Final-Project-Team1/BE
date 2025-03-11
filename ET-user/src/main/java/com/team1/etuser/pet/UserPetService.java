package com.team1.etuser.pet;

import com.team1.etuser.pet.dto.PetRes;
import com.team1.etuser.user.client.PetClient;
import com.team1.etuser.user.domain.User;
import com.team1.etuser.pet.domain.UserPet;
import com.team1.etuser.user.dto.PetGrantReq;
import com.team1.etuser.pet.dto.UserPetRes;
import com.team1.etuser.pet.dto.UserUniquePetRes;
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
    private final PetClient petClient;

    public List<UserPetRes> getUserPets(Long userId) {
        List<UserPet> userPets = userPetRepository.findByUserId(userId);
        return userPets.stream()
                .map(userPet -> new UserPetRes(userPet.getId(), userPet.getPetId()))
                .collect(Collectors.toList());
    }

    public UserPet grantPet(Long userId, PetGrantReq requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserPet userPet = UserPet.builder()
                .user(user)
                .petId(requestDto.getPetId())
                .build();
        userPetRepository.save(userPet);

        return userPet;
    }

    public List<UserUniquePetRes> getUniquePetsByUser(Long userId) {
        List<UserPet> userPets = userPetRepository.findByUserId(userId);

        // 중복 제거 (Set 사용)
        Set<Long> uniquePetIds = new HashSet<>();
        return userPets.stream()
                .filter(userPet -> uniquePetIds.add(userPet.getPetId())) // 중복 제거
                .map(userPet -> {
                    PetRes petRes = petClient.getPetById(userPet.getPetId()); // ET-Arcade에서 pet 정보 가져오기
                    return new UserUniquePetRes(userPet.getPetId(), petRes.getImg()); // Pet 이미지 포함
                })
                .collect(Collectors.toList());
    }

}


