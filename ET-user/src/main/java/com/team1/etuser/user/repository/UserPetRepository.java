package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.UserPet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPetRepository extends JpaRepository<UserPet, Long> {
    List<UserPet> findByUserId(Long userId);
}
