package com.team1.etarcade.pet.controller;

import com.team1.etarcade.pet.domain.Pet;
import com.team1.etarcade.pet.dto.PetGrantResponseDTO;
import com.team1.etarcade.pet.repository.PetRepository;
import com.team1.etarcade.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final PetRepository petRepository;

    @GetMapping
    public ResponseEntity<List<Pet>> getUserPets(@RequestHeader("X-Id") Long userId) {
        List<Pet> pets = petService.getUserPets(userId);
        return ResponseEntity.ok(pets);
    }

    @PostMapping
    public ResponseEntity<PetGrantResponseDTO> grantRandomPet(@RequestHeader("X-Id") Long userId) {
        Long petId = petService.grantRandomPet(userId).getPetId();
        String img = petRepository.findPetById(petId).getImg();
        return ResponseEntity.ok(new PetGrantResponseDTO(petId, img));
    }

    @GetMapping("/{subscribedId}")
    public ResponseEntity<List<Pet>> getSubscriberPets(@RequestHeader("X-Id") Long userId,
                                                       @PathVariable("subscribedId") Long subscribedId) {
        List<Pet> pets = petService.getSubscriberPets(userId, subscribedId);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/search/{petId}")
    public ResponseEntity<Pet> getPetById(@PathVariable("petId") Long petId) {
        Pet pet = petService.getPetById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with ID: " + petId));
        return ResponseEntity.ok(pet);
    }

}
