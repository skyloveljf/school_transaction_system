package com.example.yourproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 会话实体类，对应CONVERSATIONS表
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "CONVERSATIONS")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long conversationId;
    
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;
    
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;
    
    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;
    
    // 创建静态工厂方法方便创建新的会话
    public static Conversation createNew(Long user1Id, Long user2Id) {
        Conversation conversation = new Conversation();
        conversation.setUser1Id(user1Id);
        conversation.setUser2Id(user2Id);
        conversation.setLastMessageTime(LocalDateTime.now());
        return conversation;
    }
    
    // 判断用户是否是会话参与者
    public boolean hasParticipant(Long userId) {
        return userId != null && (user1Id.equals(userId) || user2Id.equals(userId));
    }
    
    // 获取会话中的另一个用户
    public Long getOtherUserId(Long currentUserId) {
        if (user1Id.equals(currentUserId)) {
            return user2Id;
        } else if (user2Id.equals(currentUserId)) {
            return user1Id;
        }
        return null;
    }
} 