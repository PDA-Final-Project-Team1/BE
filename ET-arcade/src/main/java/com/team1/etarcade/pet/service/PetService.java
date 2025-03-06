package com.team1.etarcade.pet.service;

import com.team1.etarcade.pet.domain.Pet;
import com.team1.etarcade.pet.dto.PetGrantRequestDTO;
import com.team1.etarcade.pet.dto.PetGrantResponseDTO;
import com.team1.etarcade.pet.dto.SubscriptionResponseDTO;
import com.team1.etarcade.pet.dto.UserPetResponseDTO;
import com.team1.etarcade.pet.connector.FriendFeignConnector;
import com.team1.etarcade.pet.connector.UserFeignConnector;
import com.team1.etarcade.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final UserFeignConnector userFeignConnector;
    private final FriendFeignConnector friendFeignConnector;
    private final PetRepository petRepository;

    public List<Pet> getUserPets(Long userId) {
        List<UserPetResponseDTO> userPets = userFeignConnector.getUserPets(userId);
        return userPets.stream()
                .map(userPet -> petRepository.findById(userPet.getPetId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 구독자의 목장을 조회하는 로직
    public List<Pet> getSubscriberPets(Long currentUserId, Long subscribeId) {
        // 1. 현재 사용자의 구독 목록 조회
        SubscriptionResponseDTO subscriptionResponse = friendFeignConnector.getSubscriptions(currentUserId);
        boolean isSubscribed = false;
        if (subscriptionResponse.getFriends() != null) {
            isSubscribed = subscriptionResponse.getFriends().stream()
                    .anyMatch(friend -> friend.getSubscribedId().equals(subscribeId));
        }
        if (!isSubscribed) {
            throw new RuntimeException("The specified user is not subscribed.");
        }
        // 2. 구독 대상 사용자의 펫 정보를 조회
        List<UserPetResponseDTO> userPets = userFeignConnector.getUserPets(subscribeId);
        return userPets.stream()
                .map(userPet -> petRepository.findById(userPet.getPetId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 기존 랜덤 펫 지급 로직
    public PetGrantResponseDTO grantRandomPet(Long userId) {
        List<Pet> allPets = petRepository.findAll();
        if (allPets.isEmpty()) {
            throw new RuntimeException("No available pets to grant.");
        }
        int randomIndex = (int) (Math.random() * allPets.size());
        Pet selectedPet = allPets.get(randomIndex);
        return userFeignConnector.grantPet(userId, new PetGrantRequestDTO(selectedPet.getId()));
    }
}
