package com.team1.etarcade.quiz;

import com.team1.etarcade.egg.domain.Egg;
import com.team1.etarcade.quiz.domain.Difficulty;
import com.team1.etarcade.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByDifficulty(Difficulty difficulty);
    Quiz findQuizById(Long id);
}