package com.example.rag.ToolService;

import org.springframework.ai.tool.annotation.Tool;

public class DateTimeTool {

    @Tool(description = "Returns the current date and time in the format yyyy-MM-dd HH:mm:ss")
    public String getCurrentDateTime() {
        return java.time.LocalDateTime.now().toString();
    }
}
