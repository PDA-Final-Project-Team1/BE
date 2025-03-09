package com.team1.etuser.user.connector;

import com.team1.etuser.user.domain.Pet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ET-arcade", path = "/api/pets", contextId = "petFeignConnector")
public interface PetFeignConnector {
    @GetMapping("/search/{petId}")
    Pet getPetById(@PathVariable("petId") Long petId);
}
