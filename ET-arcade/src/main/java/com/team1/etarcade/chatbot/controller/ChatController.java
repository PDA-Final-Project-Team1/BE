package com.team1.etarcade.chatbot.controller;

import com.team1.etarcade.chatbot.service.ChatGPTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatGPTService chatGPTService;

    public ChatController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestHeader("X-Id") Long userId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(chatGPTService.processUserMessage(userId, request.get("message")));
    }
}
