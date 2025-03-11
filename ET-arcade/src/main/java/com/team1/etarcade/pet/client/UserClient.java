package com.team1.etarcade.pet.client;

import com.team1.etarcade.pet.dto.PetGrantReq;
import com.team1.etarcade.pet.dto.PetGrantRes;
import com.team1.etarcade.pet.dto.UserPetRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;

@FeignClient(name = "ET-user", path = "/api/users", contextId = "userPetFeignConnector")
public interface UserClient {
    @GetMapping("/pets")
    List<UserPetRes> getUserPets(@RequestHeader("X-Id") Long userId);

    @PostMapping("/pets")
    PetGrantRes grantPet(@RequestHeader("X-Id") Long userId, @RequestBody PetGrantReq requestDto);
}

