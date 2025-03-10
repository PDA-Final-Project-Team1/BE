package com.team1.etarcade.quiz.controller;

import com.team1.etarcade.quiz.domain.Difficulty;
import com.team1.etarcade.quiz.dto.QuizDTO;
import com.team1.etarcade.quiz.dto.QuizSubmitResponseDTO;
import com.team1.etarcade.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.team1.etarcade.quiz.dto.QuizSubmitRequestDTO;

@RestController
@RequestMapping("/api/quizs")
@RequiredArgsConstructor
public class QuizController {


    private final QuizService quizService;


//    @GetMapping("/{userId}/quiz-status")
//    public ResponseEntity<QuizStatusResponseDto> getQuizStatus(@PathVariable Long userId) {
//        QuizStatusResponseDto response = quizService.getQuizStatus(userId);
//        return ResponseEntity.ok(response);
//    }
    @GetMapping("")
    public ResponseEntity<QuizDTO> getQuizByDifficulty(@RequestParam Difficulty difficulty) {

        QuizDTO quiz = quizService.getQuizByDifficulty(difficulty);
        return ResponseEntity.ok(quiz);
    }
    @PostMapping("/submit")
    public ResponseEntity<QuizSubmitResponseDTO> submitQuiz(@RequestBody QuizSubmitRequestDTO request) {
        QuizSubmitResponseDTO response = quizService.submitQuiz(request);
        return ResponseEntity.ok(response);
    }

}
