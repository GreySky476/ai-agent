package com.example.aiagent.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyHumanSupport(String sessionId, String message) {
        String payload = String.format("{\"sessionId\": \"%s\", \"message\": \"%s\"}", sessionId, message);
        messagingTemplate.convertAndSend("/topic/support", payload);
    }
}

