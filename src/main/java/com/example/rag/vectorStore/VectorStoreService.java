package com.example.rag.vectorStore;

import jakarta.annotation.PostConstruct;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;

import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class VectorStoreService {
    private static final Logger log = LoggerFactory.getLogger(VectorStoreService.class);
    @Value("classpath:/docs/cricket.txt")
    private Resource faq;

    @Value("classpath:/docs/*")
    private Resource[] faqFiles;


    private final VectorStore vectorStore;
    public VectorStore getVectorStore() {
        return vectorStore;
    }

    public VectorStoreService(Resource[] faqFiles, VectorStore vectorStore) {
        this.faqFiles = faqFiles;
        this.vectorStore = vectorStore;

    }

    //add document ot vector store
    @PostConstruct
    public void generateVectorStore(){
        String sourceId = "cricket-doc-v1";

        // Search by sourceId to check if data already exists
        List<Document> existingDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(sourceId)
                        .topK(1)
                        .build()
        );

        if (existingDocs != null && !existingDocs.isEmpty()) {
            log.info("Vector store already contains data with sourceId={}, skipping insertion.", sourceId);
            return;
        }
        TextReader textReader = new TextReader(faq);
        List<Document> documents = textReader.get();
        log.debug("Documents read: {}", documents.size());
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> splitDocuments = textSplitter.apply(documents);
        // Add metadata to each split document
        for (Document doc : splitDocuments) {
            doc.getMetadata().put("sourceId", sourceId);
        }
        log.debug("Documents split: {}", splitDocuments.size());
        vectorStore.add(splitDocuments);
    }

    //generate vector store from all faq files stored in docs
    @PostConstruct
    public void generateVectorStoreForAllFiles(){
        List<Document> allSplitDocuments = new ArrayList<>();
        TextSplitter textSplitter = new TokenTextSplitter();

        for (Resource resource : faqFiles) {
            String sourceId = resource.getFilename();

            // Optional: skip if already present based on metadata? (implement if needed)
            if(isVectorStoreAlreadyExist(sourceId)){
                continue;
            }
            // Split docs into chunks
            List<Document> splitDocs = getSplitText(resource);

            // Add metadata to each chunk for traceability
            for (Document doc : splitDocs) {
                doc.getMetadata().put("sourceId", sourceId);
            }
            log.debug("Split into {} documents from {}", splitDocs.size(), sourceId);
            allSplitDocuments.addAll(splitDocs);
        }
        if (!allSplitDocuments.isEmpty()) {
            vectorStore.add(allSplitDocuments);
            log.info("Added {} documents from {} files to vector store.", allSplitDocuments.size(), faqFiles.length);
        } else {
            log.info("No documents found to add to vector store.");
        }

    }

    //return true if vector store already contains data with sourceId
    private boolean isVectorStoreAlreadyExist(String sourceId) {
        List<Document> existingDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(sourceId)   // searching by sourceId as query
                        .topK(1)
                        .build()
        );
        if(existingDocs==null || existingDocs.isEmpty()){
            return false;
        }
        return existingDocs.stream()
                .anyMatch(doc -> sourceId.equals(doc.getMetadata().get("sourceId")));
    }

    //return a list of documents with the highest cosine similarity to the prompt text
    public List<Document> similaritySearch(String promptText){
        return vectorStore.similaritySearch(SearchRequest.builder().query(promptText).topK(3).similarityThreshold(0.4d).build());
    }

    public List<Document> getSplitText(Resource resource){
        TextSplitter textSplitter = new TokenTextSplitter(300,50,5,10000,true);
        return textSplitter.apply(new TextReader(resource).get());
    }

    //return a list of doc of any document type in resources
    public List<Document> readAnyDocument( Resource resource) throws IOException, TikaException {
        if(resource==null){
            log.debug("Resource is null");
            return List.of();
        }
        Tika tika = new Tika();
        String text = tika.parseToString(resource.getInputStream());
        Document doc = new Document(text, Map.of("sourceId", Objects.requireNonNull(resource.getFilename())));
        return List.of(doc);
    }


}
