package com.example.rag.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AnthropicChatController {

    private final ChatClient chatClient;

    public AnthropicChatController(@Qualifier("AnthropicChatClient") ChatClient chatClient) {
        this.chatClient= chatClient;
    }

    @GetMapping("/claude/chat")
    String chat(@RequestParam(required = false) String promptText) {
        ChatResponse chatResponse = chatClient.prompt().user(promptText).call().chatResponse();
        assert chatResponse != null;
        return chatResponse.getResult().getOutput().getText();
    }
}
