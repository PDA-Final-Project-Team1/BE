package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
