package com.example.rag.controller;

import com.example.rag.ollamaService.ollamaChatService;
import com.example.rag.vectorStore.VectorStoreService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ollamaChatController {
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ollamaChatController.class);
    private final ollamaChatService ollamaChatService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/test")
    public String test() {
        return "chat";
    }
    public ollamaChatController(ollamaChatService ollamaChatService,VectorStoreService vectorStoreService) {
        this.ollamaChatService = ollamaChatService;
    }


    @GetMapping("ollama/chat")
    public String ollamaChat() {
        //return ollamaChatService.generateResponse(promptText);
        return "chat";
    }

    @HxRequest
    @PostMapping("/ollama/chat")
    public HtmxResponse generateResponse(@RequestParam String promptText, Model model) {
        log.info("User Message: {}", promptText);
        String response = ollamaChatService.generateResponse(promptText);

        model.addAttribute("response",response);
        model.addAttribute("message",promptText);

        return HtmxResponse.builder()
                .view("response :: responseFragment")
                .build();
    }

    @GetMapping("/rag/chat")
    public String getRagQuery(@RequestParam(required = false) String promptText,HttpServletRequest request){
        return ollamaChatService.generateRAGResponse(promptText,request);
    }


}
