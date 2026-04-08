package com.example.aiagent.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.TokenStream;

public interface AiAssistant {

    @SystemMessage({
        "You are a professional AI customer service agent.",
        "Use the provided knowledge base to answer user questions.",
        "If you cannot find the answer, politely tell the user you don't know and offer to transfer to a human agent.",
        "Be concise and friendly."
    })
    String chat(String userMessage);
}

