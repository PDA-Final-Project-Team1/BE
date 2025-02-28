package com.team1.etarcade.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizStatusResponseDTO {
    private boolean hasSubmittedToday;
    private Long lastQuizId;
    private String lastQuizTitle;
    private String submittedAt;
    private String nextQuizAvailableIn;
}
