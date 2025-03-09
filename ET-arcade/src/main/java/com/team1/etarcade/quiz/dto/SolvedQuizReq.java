package com.team1.etarcade.quiz.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public class SolvedQuizReq {
    private Long quizId;
    private boolean userAnswer;
}
