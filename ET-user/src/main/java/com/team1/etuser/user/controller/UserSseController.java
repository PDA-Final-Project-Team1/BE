package com.team1.etuser.user.controller;

import com.team1.etuser.user.dto.EvaluationSseDTO;
import com.team1.etuser.user.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class UserSseController {
    private final EvaluationService evaluationService;

    @GetMapping("/evaluation")
    public SseEmitter streamEvaluationData() {
//        EvaluationSseDTO getEvaluationData();
    }
}
