package com.example.rag.RagService;

import com.example.rag.vectorStore.VectorStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RagService {
    private static final Logger log = LoggerFactory.getLogger(RagService.class);
    private final VectorStoreService vectorStoreService;

    RagService(VectorStoreService vectorStoreService){
        this.vectorStoreService = vectorStoreService;
    }

    public QuestionAnswerAdvisor getQuestionAnswerAdvisor(String promptText, Map<String,Object> inputs){
        PromptTemplate customPromptTemplate = getCustomPromptTemplate(inputs);
        //log.debug("custom prompt template render input: {}", customPromptTemplate.render());
        return QuestionAnswerAdvisor.builder(vectorStoreService.getVectorStore())
                .promptTemplate(customPromptTemplate)
                //.searchRequest(SearchRequest.builder().similarityThreshold(0.4d).topK(2).build())
                .build();
    }

    public PromptTemplate getCustomPromptTemplate(Map<String,Object> inputs) {
        return PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("""
                Context information is provided below.

                ```
                context: <question_answer_context>
                ```

                Given this context and the question below, provide a concise and relevant answer.

                Question: <query>

                Rules:
                1. If the answer is not in the context and chat history, say "I don't know".
                2. Do not mention that the information is from context or documents.
                3. Be accurate, complete, and to the point.
                """)
                .variables(inputs)
                .build();


    }



    //naive RAG

}
