package com.team1.etuser.user.client;

import com.team1.etuser.pet.dto.PetRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ET-arcade", path = "/api/pets", contextId = "petFeignConnector")
public interface PetClient {
    @GetMapping("/search/{petId}")
    PetRes getPetById(@PathVariable("petId") Long petId);
}
