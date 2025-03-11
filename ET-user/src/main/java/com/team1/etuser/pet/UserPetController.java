package com.team1.etuser.pet;

import com.team1.etuser.pet.domain.UserPet;
import com.team1.etuser.pet.dto.UserPetRes;
import com.team1.etuser.pet.dto.UserUniquePetRes;
import com.team1.etuser.user.dto.PetGrantReq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/pets")
@RequiredArgsConstructor
public class UserPetController {

    private final UserPetService  userPetService;

    @GetMapping("")
    public ResponseEntity<List<UserPetRes>> getUserPets(@RequestHeader("X-Id") Long userId) {
        List<UserPetRes> pets = userPetService.getUserPets(userId);
        return ResponseEntity.ok(pets);
    }

    @PostMapping("")
    public ResponseEntity<UserPet> grantPet(@RequestHeader("X-Id") Long userId,
                                            @RequestBody PetGrantReq requestDto) {
        UserPet userPet = userPetService.grantPet(userId, requestDto);
        return ResponseEntity.ok(userPet);
    }

    @GetMapping("/unique")
    public ResponseEntity<List<UserUniquePetRes>> getUniquePetsByUser(@RequestHeader("X-Id") Long userId) {
        List<UserUniquePetRes> pets = userPetService.getUniquePetsByUser(userId);
        return ResponseEntity.ok(pets);
    }
}
