

-- Create the chat memory table as expected by Spring AI JDBC repository
CREATE TABLE spring_ai_chat_memory (
    id SERIAL PRIMARY KEY,
    conversation_id VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    name VARCHAR(255),
    type VARCHAR(50),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast lookup by conversation ID
CREATE INDEX idx_conversation_id ON spring_ai_chat_memory(conversation_id);
