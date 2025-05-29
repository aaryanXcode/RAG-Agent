package com.example.rag.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class RagConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RagConfiguration.class);


    @Bean
    public PgVectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                /*.dimensions(765)                    // Optional: defaults to model dimensions or 1536 //thes ethings defined in properties so no need to define here again,just build it
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                .indexType(HNSW)          */           // Optional: defaults to HNSW
                .initializeSchema(true)              // Optional: defaults to false
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(10000)
                // Optional: defaults to 10000
                .build();
    }

}
