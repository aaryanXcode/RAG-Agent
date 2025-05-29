package com.example.rag.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OpenAIChatController {

    private final ChatClient chatClient;

    public OpenAIChatController(@Qualifier("OpenAiChatClient") ChatClient chatClient) {
        this.chatClient= chatClient;
    }

    @GetMapping("/openai/chat")
    String chat(@RequestParam(required = false) String promptText) {
        ChatResponse chatResponse = chatClient.prompt().user(promptText).call().chatResponse();
        assert chatResponse != null;
        return chatResponse.getResult().getOutput().getText();
    }
}
