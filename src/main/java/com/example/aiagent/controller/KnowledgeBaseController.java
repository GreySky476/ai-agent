package com.example.aiagent.controller;

import com.example.aiagent.service.ai.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/backend/knowledge")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    /**
     * Upload a document to the knowledge base.
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }

        try {
            knowledgeBaseService.ingestDocument(file);
            return ResponseEntity.ok("Document uploaded and ingested successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to process document: " + e.getMessage());
        }
    }

    /**
     * Clear the knowledge base (Admin only).
     */
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> clearKnowledge() {
        knowledgeBaseService.clearKnowledgeBase();
        return ResponseEntity.ok("Knowledge base cleared");
    }
}
