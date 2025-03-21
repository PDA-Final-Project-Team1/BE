package com.team1.etarcade.quiz.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmitRes {
    private boolean isCorrect;
    private boolean correctAnswer;
    private int earnedPoints;
    private String message;
}