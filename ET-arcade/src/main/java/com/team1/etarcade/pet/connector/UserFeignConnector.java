package com.team1.etarcade.pet.connector;

import com.team1.etarcade.pet.dto.UserPetResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;

@FeignClient(name = "ET-user", path = "/api/users", contextId = "userPetFeignConnector")
public interface UserFeignConnector {
    @GetMapping("/pets")
    List<UserPetResponseDTO> getUserPets(@RequestHeader("X-Id") Long userId);
}

