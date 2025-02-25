package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserFavoriteStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFavoriteStockRepository extends JpaRepository<UserFavoriteStock, Long> {
}
