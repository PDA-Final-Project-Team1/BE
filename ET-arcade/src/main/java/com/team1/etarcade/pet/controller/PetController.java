package com.team1.etarcade.pet.controller;

import com.team1.etarcade.pet.domain.Pet;
import com.team1.etarcade.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public ResponseEntity<List<Pet>> getUserPets(@RequestHeader("X-Id") Long userId) {
        List<Pet> pets = petService.getUserPets(userId);
        return ResponseEntity.ok(pets);
    }

    @PostMapping
    public ResponseEntity<Void> grantRandomPet(@RequestHeader("X-Id") Long userId) {
        petService.grantRandomPet(userId);
        return ResponseEntity.ok().build();
    }
}
