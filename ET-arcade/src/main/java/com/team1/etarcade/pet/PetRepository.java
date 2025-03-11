package com.team1.etarcade.pet;

import com.team1.etarcade.pet.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Pet findPetById(Long id);
}
