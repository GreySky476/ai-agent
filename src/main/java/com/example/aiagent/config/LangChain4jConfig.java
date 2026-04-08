package com.example.aiagent.config;

import com.example.aiagent.service.ai.AiAssistant;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.pgvector.host}")
    private String pgHost;

    @Value("${langchain4j.pgvector.port}")
    private Integer pgPort;

    @Value("${langchain4j.pgvector.user}")
    private String pgUser;

    @Value("${langchain4j.pgvector.password}")
    private String pgPassword;

    @Value("${langchain4j.pgvector.database}")
    private String pgDatabase;

    @Value("${langchain4j.pgvector.table}")
    private String pgTable;

    @Value("${langchain4j.pgvector.dimension}")
    private Integer pgDimension;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName("openai/text-embedding-3-small") // OpenRouter check for availability
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host(pgHost)
                .port(pgPort)
                .user(pgUser)
                .password(pgPassword)
                .database(pgDatabase)
                .table(pgTable)
                .dimension(pgDimension)
                .build();
    }

    @Bean
    public AiAssistant aiAssistant(ChatLanguageModel chatLanguageModel, ContentRetriever contentRetriever) {
        return AiServices.builder(AiAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemoryProvider(chatId -> MessageWindowChatMemory.builder()
                        .id(chatId)
                        .maxMessages(10)
                        .build())
                .build();
    }

    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.5)
                .build();
    }
}
