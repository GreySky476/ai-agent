package com.example.aiagent.service.ai;

import com.example.aiagent.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatBotService {

    @Autowired
    private AiAssistant assistant;

    @Autowired
    private NotificationService notificationService;

    public String chat(String sessionId, String userMessage) {
        // Trigger human support if message contains "human"
        if (userMessage.toLowerCase().contains("human") || userMessage.contains("人工")) {
            notificationService.notifyHumanSupport(sessionId, "User requested human support: " + userMessage);
            return "收到！已为您呼叫人工客服，请稍候。";
        }

        // Use LangChain4j AiService for RAG and Chat
        return assistant.chat(userMessage);
    }
}
