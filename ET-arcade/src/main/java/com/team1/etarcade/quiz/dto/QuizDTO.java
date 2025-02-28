package com.team1.etarcade.quiz.dto;

import com.team1.etarcade.quiz.domain.Difficulty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {

    private Long id;

    private String title;

    private Difficulty difficulty;

    private boolean answer;

}