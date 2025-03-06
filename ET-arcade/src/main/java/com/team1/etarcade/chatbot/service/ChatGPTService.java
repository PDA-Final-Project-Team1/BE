package com.team1.etarcade.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatGPTService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String getChatGPTResponse(List<Map<String, String>> userMessages) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, String> systemMessage = Map.of("role", "system", "content",
                "너는 주식 퀴즈에 대한 힌트를 주고 주식 교육을 도와주는 챗봇이야. "
                        + "이름은 '트레이드타운봇'이야. 짧고 간결하게 100 토큰 이내로 존댓말로 친절하게 답변해.");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.addAll(userMessages);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", messages,
                "max_tokens", 100
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);

        if (response.getBody() != null) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                return (String) message.get("content");
            }
        }
        return "응답을 가져오지 못했습니다.";
    }
}
