package com.team1.etarcade.quiz.controller;

import com.team1.etarcade.quiz.domain.Difficulty;
import com.team1.etarcade.quiz.dto.*;
import com.team1.etarcade.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/quizs")
@RequiredArgsConstructor
public class QuizController {


    private final QuizService quizService;



    @GetMapping
    public ResponseEntity<QuizDTO> getQuizByDifficulty(@RequestHeader("X-Id") Long userId, @RequestParam Difficulty difficulty) {

        QuizDTO quiz = quizService.getQuizByDifficulty(userId,difficulty);
        return ResponseEntity.ok(quiz);
    }
    @PostMapping()
    public ResponseEntity<QuizSubmitResponseDTO> submitQuiz(@RequestHeader("X-Id") Long userId, @RequestBody QuizSubmitRequestDTO request) {
        QuizSubmitResponseDTO response = quizService.submitQuiz(userId,request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/solved")
    public ResponseEntity<SolvedQuizRes> getSolvedQuiz(@RequestHeader("X-Id") Long userId,@RequestBody SolvedQuizReq request) {
        log.info("getSolvedQuiz() 요청 - userId: {}, quizId: {}", userId, request.getQuizId());

        SolvedQuizRes response = quizService.getSolvedQuiz(userId,request);

        return ResponseEntity.ok(response);

    }

}
