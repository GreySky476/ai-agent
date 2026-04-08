package com.example.aiagent.service.ai;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class KnowledgeBaseService {

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    /**
     * Ingest a document file into the vector database.
     */
    public void ingestDocument(MultipartFile file) throws IOException {
        DocumentParser parser = new ApachePdfBoxDocumentParser();
        Document document;
        try (InputStream inputStream = file.getInputStream()) {
            document = parser.parse(inputStream);
        }

        // Add metadata to identify the source
        document.metadata().add("file_name", file.getOriginalFilename());

        DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(document);
    }

    /**
     * Clear the entire knowledge base.
     * Note: PgVectorEmbeddingStore doesn't always support simple 'removeAll' via metadata in all versions,
     * but for a 1-person MVP, clearing or re-indexing is common.
     */
    public void clearKnowledgeBase() {
        // Implementation depends on the specific store capability.
        // For PgVector, one might need to execute a TRUNCATE via JDBC if not supported by LangChain4j yet.
    }
}

