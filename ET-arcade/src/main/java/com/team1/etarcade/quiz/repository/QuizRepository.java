package com.team1.etarcade.quiz.repository;

import com.team1.etarcade.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
