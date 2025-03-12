package com.team1.etarcade.quiz.dto;

import com.team1.etarcade.quiz.domain.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SolvedQuizRes {
    private Long solvedQuizId;
    private String solvedQuizTitle;
    private Difficulty solvedQuizDifficulty;
    private Integer currentUserPoints;
    private Boolean quizanswer;
}