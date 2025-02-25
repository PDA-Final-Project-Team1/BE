package com.team1.etuser.user.repository;

import com.team1.etuser.user.domain.User;
import com.team1.etuser.user.domain.UserAdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdditionalInfoRepository extends JpaRepository<UserAdditionalInfo, Long> {
}
