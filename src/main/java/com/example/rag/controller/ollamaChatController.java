package com.example.rag.controller;

import com.example.rag.ollamaService.ollamaChatService;
import com.example.rag.vectorStore.VectorStoreService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ollamaChatController {
    private final ollamaChatService ollamaChatService;

    @Autowired
    private HttpServletRequest request;
    public ollamaChatController(ollamaChatService ollamaChatService,VectorStoreService vectorStoreService) {
        this.ollamaChatService = ollamaChatService;
    }

    @GetMapping("/ollama/chat")
    public String ollamaChat(@RequestParam(required = false) String promptText,HttpServletRequest request) {
        return ollamaChatService.generateResponse(promptText);
    }

    @GetMapping("/rag/chat")
    public String getRagQuery(@RequestParam(required = false) String promptText,HttpServletRequest request){
        return ollamaChatService.generateRAGResponse(promptText,request);
    }


}
