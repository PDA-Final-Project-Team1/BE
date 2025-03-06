package com.team1.etarcade.chatbot.controller;

import com.team1.etarcade.chatbot.service.ChatGPTService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatGPTService chatGPTService;

    private final Map<Long, List<Map<String, String>>> userChatHistory = new ConcurrentHashMap<>();

    public ChatController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @PostMapping
    public String chat(@RequestHeader("X-Id") Long userId, @RequestBody Map<String, String> request) {
        if (userId == null) {
            return "X-Id 헤더가 필요합니다.";
        }

        String userMessage = request.get("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "메시지가 비어 있습니다.";
        }

        List<Map<String, String>> messages = userChatHistory.computeIfAbsent(userId, k -> new ArrayList<>());

        messages.add(Map.of("role", "user", "content", userMessage));

        String response = chatGPTService.getChatGPTResponse(messages);

        messages.add(Map.of("role", "assistant", "content", response));

        return response;
    }
}
