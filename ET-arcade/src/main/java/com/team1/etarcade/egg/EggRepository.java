package com.team1.etarcade.egg;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.team1.etarcade.egg.domain.Egg;

import java.util.List;

@Repository
public interface EggRepository extends JpaRepository<Egg, Long> {

    List<Egg> findByUserId(Long userId);


    boolean existsByIdAndUserId(Long userId, Long eggId);

}