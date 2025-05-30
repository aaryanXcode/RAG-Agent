package com.example.rag.ollamaService;
import com.example.rag.RagService.RagService;
import com.example.rag.ToolService.DateTimeTool;
import com.example.rag.vectorStore.VectorStoreService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.PostgresChatMemoryRepositoryDialect;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ollamaChatService {
    private static final Logger log = LoggerFactory.getLogger(ollamaChatService.class);
    final VectorStoreService vectorStoreService;

    private final RagService ragService;

    private final JdbcChatMemoryRepository chatMemoryRepository ;

    private final ChatMemory chatMemory ;

    private final ChatClient chatClient;

    public ollamaChatService(VectorStoreService vectorStoreService, RagService ragService, ChatMemoryRepository chatMemoryRepository, JdbcTemplate jdbcTemplate, @Qualifier("OllamaChatClient") ChatClient chatClient) {
        this.vectorStoreService = vectorStoreService;
        this.ragService=ragService;
        this.chatClient=chatClient;
        this.chatMemoryRepository = JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(new PostgresChatMemoryRepositoryDialect())
                .build();
        this.chatMemory = MessageWindowChatMemory.builder().chatMemoryRepository(chatMemoryRepository).maxMessages(10).build();

    }

    //return simple response by LLM
    public String generateResponse(String promptText) {
        //PromptTemplate promptTemplate = new PromptTemplate(promptText);
        ChatResponse chatResponse = chatClient.prompt().user(promptText).call().chatResponse();
        assert chatResponse != null;
        String LLMResponse =  chatResponse.getResult().getOutput().getText();
        return LLMResponse.replaceAll("(?s)<think>.*?</think>", "").trim();

    }

    //return RAG chat response
    public String generateRAGResponse(String promptText, HttpServletRequest request){
        String sessionId = request.getSession().getId();
        List<Document> results = vectorStoreService.similaritySearch(promptText);
        //log.debug("Docs retrieved: " + results);
        String context = results.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n"));

        //chat history based on session id
        List<Message> chatMemoryByConversationId = chatMemory.get(sessionId);

        // Build your input variables map:
        Map<String, Object> inputs = Map.of(
                "question_answer_context", context,
                "query", promptText
        );

        QuestionAnswerAdvisor questionAdvisor = ragService.getQuestionAnswerAdvisor(promptText,inputs);
        chatMemory.add(sessionId,new UserMessage(promptText));
        //ChatResponse chatResponse = ChatClient.builder(chatModel).build().prompt().advisors(new QuestionAnswerAdvisor(vectorStore)).user(promptText).call().chatResponse();
        ChatResponse chatResponse = chatClient.prompt().tools(new DateTimeTool()).advisors(questionAdvisor,MessageChatMemoryAdvisor.builder(chatMemory).conversationId(sessionId).build()).user(promptText).call().chatResponse();

        assert chatResponse != null;
        log.debug("Chat response: {}", chatResponse.getResult().getOutput().getText());
        chatMemory.add(sessionId,new AssistantMessage(chatResponse.getResult().getOutput().getText()));
        System.out.println("Chat response: "+chatResponse.toString());
        String LLMResponse =  chatResponse.getResult().getOutput().getText();
        String cleanResponse = LLMResponse.replaceAll("(?s)<think>.*?</think>", "").trim();
        log.debug("Clean response: {}", cleanResponse);
        return cleanResponse;


    }


}
