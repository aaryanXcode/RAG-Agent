package com.example.rag.configuration;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfiguration {

    @Bean
    ChatClient OllamaChatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    ChatClient OpenAiChatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    ChatClient AnthropicChatClient(AnthropicChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
