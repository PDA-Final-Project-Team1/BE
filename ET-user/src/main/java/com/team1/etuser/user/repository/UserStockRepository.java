package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStockRepository extends JpaRepository<UserStock, Long> {
}
