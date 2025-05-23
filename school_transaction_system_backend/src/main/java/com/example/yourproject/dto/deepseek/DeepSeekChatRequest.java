package com.example.yourproject.dto.deepseek;

import java.util.List;
import java.util.ArrayList;

public class DeepSeekChatRequest {
    private String model;
    private List<Message> messages;
    private boolean stream = false; // 默认为非流式

    public DeepSeekChatRequest(String model, String systemContent, String userContent) {
        this.model = model;
        this.messages = new ArrayList<>();
        if (systemContent != null && !systemContent.isEmpty()) {
            this.messages.add(new Message("system", systemContent));
        }
        this.messages.add(new Message("user", userContent));
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}