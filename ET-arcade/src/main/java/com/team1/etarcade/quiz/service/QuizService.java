package com.team1.etarcade.quiz.service;

import com.team1.etarcade.quiz.domain.Difficulty;
import com.team1.etarcade.quiz.domain.Quiz;
import com.team1.etarcade.quiz.dto.QuizDTO;
import com.team1.etarcade.quiz.dto.QuizStatusResponseDto;
import com.team1.etarcade.quiz.repository.QuizRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final Random random = new Random();

    public QuizDTO getQuizByDifficulty(Difficulty diff) {
        //레포에서 Diff에 해당하는 리스트중 랜덤으로 퀴즈하나 반환.

        List<Quiz> quizList = quizRepository.findByDifficulty(diff);
        if (quizList.isEmpty()) {
            throw new IllegalArgumentException("No quizzes found for difficulty: " + diff);
        }

        // 랜덤으로 하나 선택

        Quiz selectedQuiz = quizList.get(random.nextInt(quizList.size()));

        // QuizDTO로 변환하여 반환
        return new QuizDTO(
                selectedQuiz.getId(),
                selectedQuiz.getTitle(),
                selectedQuiz.getDifficulty(),
                selectedQuiz.getAnswer()
        );




    }



}
