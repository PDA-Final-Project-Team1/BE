package com.team1.etarcade.quiz.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QuizSubmitReq {
    private Long quizId;
    private boolean userAnswer;
}