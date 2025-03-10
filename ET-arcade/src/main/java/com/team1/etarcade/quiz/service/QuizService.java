package com.team1.etarcade.quiz.service;

import com.team1.etarcade.quiz.connector.QuizUserConnector;
import com.team1.etarcade.quiz.domain.Difficulty;
import com.team1.etarcade.quiz.domain.Quiz;
import com.team1.etarcade.quiz.dto.*;
import com.team1.etarcade.quiz.repository.QuizRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final Random random = new Random();
    private final QuizUserConnector quizUserConnector;

    public QuizDTO getQuizByDifficulty(long userId, Difficulty diff) {
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


    // 사용자 ID를 통해 POINT 증가하는 로직도 필요.

    public QuizSubmitResponseDTO submitQuiz(Long userid, QuizSubmitRequestDTO request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 퀴즈를 찾을 수 없습니다."));

        // 정답 확인
        boolean isCorrect = quiz.isAnswer() == request.isUserAnswer();

        //point 지급 로직  (임ㅁ시 )
        int earnedPoints = 0;
        if (isCorrect) { // 정답일 때만 포인트 지급
            switch (quiz.getDifficulty()) {
                case TOP:
                    earnedPoints = 100;
                    break;
                case HIGH:
                    earnedPoints = 80;
                    break;
                case MEDIUM:
                    earnedPoints = 60;
                    break;
                case LOW:
                    earnedPoints = 20;
                    break;
                default:
                    earnedPoints = 0; // 혹시 모를 예외 상황 처리
            }
        }

        //point 업데이트 요청 전달
        if (isCorrect) {
            quizUserConnector.updateUserPoints(userid, earnedPoints);
        }


        return new QuizSubmitResponseDTO(
                isCorrect,
                quiz.isAnswer(),
                earnedPoints,
                isCorrect ? "퀴즈 정답 제출 완료! 오늘의 포인트를 획득했습니다." : "오답! 포인트획득에 실패하였습니다."
        );


    }
    // solvedQuizService
    public SolvedQuizRes getSolvedQuiz(Long userid, Long quizId,boolean userAnswer){
        log.info("getSolvedQuiz() 호출됨 - userId: {}, quizId: {}", userid, quizId);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 퀴즈를 찾을 수 없습니다."));

        Integer userPoint = quizUserConnector.getUserPoints(userid).getNowUserPoint();
        log.info("사용자 ID: {}, 현재 포인트: {}", userid, userPoint);

        // `SolvedQuizRes` 객체 반환
        return new SolvedQuizRes(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDifficulty(),
                userPoint
        );
    }


}