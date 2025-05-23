package com.example.yourproject.dto;

import java.time.LocalDateTime;

/**
 * 会话DTO，用于展示会话列表
 */
public class ConversationDto {
    private Long conversationId;
    private Long otherUserId; // 对方用户ID
    private String otherUsername; // 对方用户名
    private String otherUserAvatar; // 对方头像
    private String lastMessage; // 最后一条消息内容
    private LocalDateTime lastMessageTime; // 最后消息时间
    private Integer unreadCount; // 未读消息数量

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(Long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherUsername() {
        return otherUsername;
    }

    public void setOtherUsername(String otherUsername) {
        this.otherUsername = otherUsername;
    }

    public String getOtherUserAvatar() {
        return otherUserAvatar;
    }

    public void setOtherUserAvatar(String otherUserAvatar) {
        this.otherUserAvatar = otherUserAvatar;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }
} 