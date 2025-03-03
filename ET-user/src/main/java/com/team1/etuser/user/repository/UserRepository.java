package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByUid(String uid);
    boolean existsByUid(String uid);
}