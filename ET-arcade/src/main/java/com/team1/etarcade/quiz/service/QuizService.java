package com.team1.etarcade.quiz.service;

import com.team1.etarcade.quiz.domain.Difficulty;
import com.team1.etarcade.quiz.domain.Quiz;
import com.team1.etarcade.quiz.dto.QuizDTO;
import com.team1.etarcade.quiz.dto.QuizSubmitRequestDTO;
import com.team1.etarcade.quiz.dto.QuizSubmitResponseDTO;
import com.team1.etarcade.quiz.repository.QuizRepository;
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
                selectedQuiz.isAnswer()
        );




    }

    // 퀴즈 제출 로직
    // 사용자 ID를 FEGIN으로 받아오는 과정 추가 필요.
    // 현재 하드코딩 아이디로 사용
    // 사용자 ID를 통해 POINT 증가하는 로직도 필요.

    private static final Long HARDCODED_USER_ID = 1L;

    public QuizSubmitResponseDTO submitQuiz(QuizSubmitRequestDTO request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 퀴즈를 찾을 수 없습니다."));

        // 정답 확인
        boolean isCorrect = quiz.isAnswer() == request.isUserAnswer();

        //point 지급 로직  (임ㅁ시 )
        int earnedPoints = isCorrect ? 100 : 0;


        return new QuizSubmitResponseDTO(
                isCorrect,
                quiz.isAnswer(),
                earnedPoints,
                isCorrect ? "퀴즈 정답 제출 완료! 오늘의 포인트를 획득했습니다." : "퀴즈 정답 제출 완료! 포인트획득에 실패하였습니다."
        );


    }
}