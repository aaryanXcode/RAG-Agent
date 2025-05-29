package com.example.rag.Repository;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.ai.chat.memory.repository.jdbc.PostgresChatMemoryRepositoryDialect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate.*;

public class OllamaChatMemoryRepository {


    /*private final JdbcChatMemoryRepository chatMemoryRepository;
    private final ChatMemory chatMemory;

    public OllamaChatMemoryRepository(JdbcTemplate jdbcTemplate) {
        this.chatMemoryRepository = JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(new PostgresChatMemoryRepositoryDialect())
                .build();

        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(this.chatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    public ChatMemory getChatMemory() {
        return this.chatMemory;
    }*/

}
