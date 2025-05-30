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
        return ChatClient.builder(chatModel)
                // .defaultSystem("You are an intelligent AI assitant which can answer question and answer in short and detailed manner as per the context and itself knowledge base")
                .build();
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
