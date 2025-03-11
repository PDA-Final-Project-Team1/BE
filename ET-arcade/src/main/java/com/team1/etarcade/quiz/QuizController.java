package com.team1.etarcade.quiz;

import com.team1.etarcade.quiz.domain.Difficulty;
import com.team1.etarcade.quiz.dto.*;
import com.team1.etarcade.quiz.QuizService;
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
    public ResponseEntity<QuizRes> getQuizByDifficulty(@RequestHeader("X-Id") Long userId, @RequestParam Difficulty difficulty) {

        QuizRes quiz = quizService.getQuizByDifficulty(userId,difficulty);
        return ResponseEntity.ok(quiz);
    }
    @PostMapping()
    public ResponseEntity<QuizSubmitRes> submitQuiz(@RequestHeader("X-Id") Long userId, @RequestBody QuizSubmitReq request) {
        QuizSubmitRes response = quizService.submitQuiz(userId,request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/solved")
    public ResponseEntity<SolvedQuizRes> getSolvedQuiz(@RequestHeader("X-Id") Long userId,@RequestParam Long quizId,@RequestParam boolean userAnswer) {
        log.info("getSolvedQuiz() 요청 - userId: {}, quizId: {}", userId, quizId);

        SolvedQuizRes response = quizService.getSolvedQuiz(userId,quizId, userAnswer);

        return ResponseEntity.ok(response);

    }

}
