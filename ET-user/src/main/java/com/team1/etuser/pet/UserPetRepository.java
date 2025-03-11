package com.team1.etuser.pet;

import com.team1.etuser.pet.domain.UserPet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPetRepository extends JpaRepository<UserPet, Long> {
    List<UserPet> findByUserId(Long userId);
}
