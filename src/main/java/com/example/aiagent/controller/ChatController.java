package com.example.aiagent.controller;

import com.example.aiagent.service.ai.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/backend/chat")
public class ChatController {

    @Autowired
    private ChatBotService chatBotService;

    @PostMapping("/{sessionId}")
    public String chat(@PathVariable String sessionId, @RequestBody java.util.Map<String, String> body) {
        String message = body.get("message");
        System.out.println("DEBUG: ChatController RECEIVED. Session: " + sessionId + ", Message: " + message);
        try {
            String response = chatBotService.chat(sessionId, message);
            System.out.println("DEBUG: ChatController RESPONSE SUCCESS: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("DEBUG: ChatController ERROR: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
