package com.team1.etarcade.quiz.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QuizSubmitRequestDTO {
    private Long quizId;
    private boolean userAnswer;
}