package com.team1.etarcade.egg.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.team1.etarcade.egg.domain.Egg;

import java.util.Optional;

@Repository
public interface EggRepository extends JpaRepository<Egg, Long> {
    @Override
    Optional<Egg> findById(Long aLong);


}
