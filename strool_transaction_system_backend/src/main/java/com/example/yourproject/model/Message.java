package com.example.yourproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息实体类，对应MESSAGES表
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "MESSAGES")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;
    
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;
    
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;
    
    @Column(name = "content", nullable = false)
    private String content;
    
    @Column(name = "send_time")
    private LocalDateTime sendTime;
    
    @Column(name = "is_read")
    private Boolean isRead;

    // 创建一条新消息的工厂方法
    public static Message createNew(
            Long conversationId, 
            Long senderId, 
            Long receiverId, 
            String content) {
        
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());
        message.setIsRead(false);
        
        return message;
    }
} 