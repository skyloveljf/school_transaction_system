package com.example.yourproject.service.impl;

import com.example.yourproject.dto.ConversationDto;
import com.example.yourproject.dto.MessageDto;
import com.example.yourproject.mapper.ConversationMapper;
import com.example.yourproject.mapper.MessageMapper;
import com.example.yourproject.model.Conversation;
import com.example.yourproject.model.Message;
import com.example.yourproject.service.ConversationService;
import com.example.yourproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationMapper conversationMapper;
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private UserService userService;

    @Override
    public List<ConversationDto> getUserConversations(Long userId) {
        System.out.println("获取用户会话列表: userId=" + userId);
        
        // 查询用户参与的所有会话
        List<Conversation> conversations = conversationMapper.findByUserId(userId);
        System.out.println("找到会话数量: " + conversations.size());
        
        // 转换为DTO
        return conversations.stream()
                .map(conv -> convertToDto(conv, userId))
                .collect(Collectors.toList());
    }

    @Override
    public ConversationDto getConversationDetail(Long conversationId, Long userId) {
        System.out.println("获取会话详情: conversationId=" + conversationId + ", userId=" + userId);
        
        // 查询会话
        Conversation conversation = conversationMapper.findById(conversationId);
        if (conversation == null) {
            System.out.println("会话不存在: " + conversationId);
            return null;
        }
        
        // 检查用户是否是会话参与者
        if (!conversation.hasParticipant(userId)) {
            System.out.println("用户不是会话参与者: userId=" + userId + ", conversationId=" + conversationId);
        return null; 
        }
        
        return convertToDto(conversation, userId);
    }

    @Override
    @Transactional
    public ConversationDto createOrGetConversation(Long currentUserId, Long receiverId) {
        System.out.println("创建或获取会话: currentUserId=" + currentUserId + ", receiverId=" + receiverId);

        if (currentUserId == null || receiverId == null) {
            System.err.println("创建或获取会话失败: 用户ID不能为空。 currentUserId: " + currentUserId + ", receiverId: " + receiverId);
            // 根据您的错误处理策略，这里可以抛出异常或返回特定的错误DTO
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 如果是与自己对话，返回null或抛出异常
        if (currentUserId.equals(receiverId)) {
            System.out.println("不能与自己创建会话");
            // 根据您的错误处理策略，这里可以抛出异常或返回特定的错误DTO
            throw new IllegalArgumentException("不能与自己创建会话");
        }

        // 规范化用户ID顺序，确保 user1Id < user2Id
        Long user1 = Math.min(currentUserId, receiverId);
        Long user2 = Math.max(currentUserId, receiverId);
        System.out.println("规范化后的ID: user1=" + user1 + ", user2=" + user2);

        Conversation existingConversation = null;
        try {
            System.out.println("尝试查找现有会话: user1=" + user1 + ", user2=" + user2);
            existingConversation = conversationMapper.findByUsers(user1, user2);
        } catch (org.apache.ibatis.exceptions.TooManyResultsException e) {
            System.err.println("严重错误: 数据库中存在重复的会话记录，user1=" + user1 + ", user2=" + user2);
            // 记录详细的异常信息
            e.printStackTrace();
            // 针对这种情况，可以考虑返回第一个结果，或者抛出自定义异常提示管理员检查数据
            // 例如: List<Conversation> duplicates = conversationMapper.findAllByUsersForCleanup(user1, user2);
            // 然后记录duplicates的详情。
            // 这里为了服务能继续，暂时返回null或抛出更具体的业务异常
            throw new IllegalStateException("数据库中存在重复的会话记录，请联系管理员。", e);
        } catch (Exception e) {
            System.err.println("查找现有会话时发生数据库错误: user1=" + user1 + ", user2=" + user2);
            e.printStackTrace(); // 打印完整的异常堆栈
            throw new RuntimeException("查找现有会话时发生数据库错误", e);
        }
        
        if (existingConversation != null) {
            System.out.println("找到现有会话: " + existingConversation.getConversationId());
            return convertToDto(existingConversation, currentUserId); // 注意这里传 currentUserId
        }
        
        // 创建新会话
        System.out.println("创建新会话: user1=" + user1 + ", user2=" + user2);
        Conversation newConversation = new Conversation();
        newConversation.setUser1Id(user1); // 使用规范化后的ID
        newConversation.setUser2Id(user2); // 使用规范化后的ID
        newConversation.setLastMessageTime(LocalDateTime.now());
        
        try {
            // 保存会话
            conversationMapper.insert(newConversation);
            System.out.println("新会话创建成功: " + newConversation.getConversationId() + "，参与者: " + newConversation.getUser1Id() + ", " + newConversation.getUser2Id());
        } catch (Exception e) {
            System.err.println("插入新会话时发生数据库错误: user1=" + user1 + ", user2=" + user2);
            e.printStackTrace(); // 打印完整的异常堆栈
            throw new RuntimeException("插入新会话时发生数据库错误", e);
        }
        
        return convertToDto(newConversation, currentUserId); // 注意这里传 currentUserId
    }

    @Override
    public List<MessageDto> getConversationMessages(Long conversationId, Long userId, Integer page, Integer size) {
        System.out.println("获取会话消息: conversationId=" + conversationId + ", userId=" + userId + ", page=" + page + ", size=" + size);
        
        // 检查会话是否存在
        Conversation conversation = conversationMapper.findById(conversationId);
        if (conversation == null) {
            System.out.println("会话不存在: " + conversationId);
            return new ArrayList<>();
        }
        
        // 检查用户是否是会话参与者
        if (!conversation.hasParticipant(userId)) {
            System.out.println("用户不是会话参与者: userId=" + userId + ", conversationId=" + conversationId);
            return new ArrayList<>();
        }
        
        // 获取会话消息
        int offset = page * size;
        List<Message> messages = messageMapper.findByConversationId(conversationId, offset, size);
        System.out.println("找到消息数量: " + messages.size());
        
        // 转换为DTO
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageDto sendMessage(Long conversationId, Long senderId, String content) {
        System.out.println("发送消息: conversationId=" + conversationId + ", senderId=" + senderId + ", content=" + content);
        
        // 检查会话是否存在
        Conversation conversation = conversationMapper.findById(conversationId);
        if (conversation == null) {
            System.out.println("会话不存在: " + conversationId);
            return null;
        }
        
        // 检查发送者是否是会话参与者
        if (!conversation.hasParticipant(senderId)) {
            System.out.println("发送者不是会话参与者: senderId=" + senderId + ", conversationId=" + conversationId);
            return null;
        }
        
        // 确定接收者
        Long receiverId = conversation.getOtherUserId(senderId);
        if (receiverId == null) {
            System.out.println("无法确定接收者: senderId=" + senderId + ", conversationId=" + conversationId);
        return null; 
        }
        
        // 创建新消息
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());
        message.setIsRead(false);
        
        // 保存消息
        messageMapper.insert(message);
        System.out.println("消息保存成功: " + message.getMessageId());
        
        // 更新会话的最后消息时间
        conversationMapper.updateLastMessageTime(conversationId, message.getSendTime());
        
        return convertToDto(message);
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long conversationId, Long userId) {
        System.out.println("标记会话已读: conversationId=" + conversationId + ", userId=" + userId);
        
        // 检查会话是否存在
        Conversation conversation = conversationMapper.findById(conversationId);
        if (conversation == null) {
            System.out.println("会话不存在: " + conversationId);
            return;
        }
        
        // 检查用户是否是会话参与者
        if (!conversation.hasParticipant(userId)) {
            System.out.println("用户不是会话参与者: userId=" + userId + ", conversationId=" + conversationId);
            return;
        }
        
        // 标记会话中所有接收到的消息为已读
        int count = messageMapper.markAllAsRead(conversationId, userId);
        System.out.println("已标记 " + count + " 条消息为已读");
    }
    
    // 转换会话实体到DTO
    private ConversationDto convertToDto(Conversation conversation, Long currentUserId) {
        // Check for null conversation (defensive)
        if (conversation == null) {
            System.err.println("convertToDto: Conversation object is null. Cannot convert to DTO.");
            // Or throw an exception, or return a specific error DTO
            // For now, let's throw an IllegalArgumentException if this happens, as it's unexpected.
            throw new IllegalArgumentException("convertToDto: Conversation object cannot be null.");
        }
        if (currentUserId == null) {
            System.err.println("convertToDto: currentUserId is null. Cannot reliably determine other user.");
            throw new IllegalArgumentException("convertToDto: currentUserId cannot be null.");
        }

        System.out.println("convertToDto: Starting conversion for conversationId: " + conversation.getConversationId() + ", currentUserId: " + currentUserId);

        Long otherUserId = conversation.getOtherUserId(currentUserId);
        
        ConversationDto dto = new ConversationDto();
        dto.setConversationId(conversation.getConversationId());
        dto.setOtherUserId(otherUserId);
        
        if (otherUserId == null) {
            System.err.println("convertToDto: Could not determine otherUserId for conversationId: " + conversation.getConversationId() + " and currentUserId: " + currentUserId + ". User1Id: " + conversation.getUser1Id() + ", User2Id: " + conversation.getUser2Id());
            // This case might indicate a logic error in getOtherUserId or how conversation was constructed
            dto.setOtherUsername("未知用户"); // Fallback
            dto.setOtherUserAvatar(null); // Fallback
            System.err.println("convertToDto: Set otherUser to '未知用户' due to null otherUserId.");
        } else {
            // 获取对方用户信息
            try {
                if (userService == null) {
                    System.err.println("convertToDto: FATAL - userService is null! Cannot fetch user details for otherUserId: " + otherUserId);
                    dto.setOtherUsername("服务内部错误"); // Indicate a system-level problem
                    dto.setOtherUserAvatar(null);
                } else {
                    System.out.println("convertToDto: Attempting to get username for otherUserId: " + otherUserId);
                    String otherUsername = userService.getUsernameById(otherUserId);
                    dto.setOtherUsername(otherUsername != null ? otherUsername : "用户" + otherUserId); // Handle null username gracefully
                    System.out.println("convertToDto: Successfully got username: " + dto.getOtherUsername() + " for otherUserId: " + otherUserId);

                    System.out.println("convertToDto: Attempting to get avatar for otherUserId: " + otherUserId);
                    String otherUserAvatar = userService.getUserAvatarById(otherUserId);
                    dto.setOtherUserAvatar(otherUserAvatar); // Avatar can be null, which is acceptable
                    System.out.println("convertToDto: Successfully got avatar (null is acceptable): " + (otherUserAvatar != null ? otherUserAvatar : "null") + " for otherUserId: " + otherUserId);
                }
            } catch (Exception e) {
                System.err.println("convertToDto: Exception while fetching user details for otherUserId: " + otherUserId + ". Error Type: " + e.getClass().getName() + ", Error Message: " + e.getMessage());
                e.printStackTrace(); // Print full stack trace to understand the origin of "null" message
                dto.setOtherUsername("用户" + otherUserId); // Fallback username
                dto.setOtherUserAvatar(null); // Fallback avatar
                System.err.println("convertToDto: Set fallback user details for otherUserId: " + otherUserId + " due to exception.");
            }
        }
        
        // 设置最后消息内容
        // 当前实现是简化版，如果需要，可以查询 Message 表获取最后一条消息的内容
        dto.setLastMessage(""); 
        dto.setLastMessageTime(conversation.getLastMessageTime());
        System.out.println("convertToDto: Set lastMessage and lastMessageTime for conversationId: " + dto.getConversationId());
        
        // 获取未读消息数
        try {
            if (messageMapper == null) {
                System.err.println("convertToDto: FATAL - messageMapper is null! Cannot fetch unread count for conversationId: " + conversation.getConversationId());
                dto.setUnreadCount(0); // Fallback
            } else {
                System.out.println("convertToDto: Attempting to get unread count for conversationId: " + conversation.getConversationId() + ", receiverId (currentUserId): " + currentUserId);
                int unreadCount = messageMapper.countUnreadByConversationAndReceiverId(conversation.getConversationId(), currentUserId);
                dto.setUnreadCount(unreadCount);
                System.out.println("convertToDto: Successfully got unread count: " + unreadCount + " for conversationId: " + dto.getConversationId());
            }
        } catch (Exception e) {
            System.err.println("convertToDto: Exception while fetching unread message count for conversationId: " + conversation.getConversationId() + ". Error Type: " + e.getClass().getName() + ", Error Message: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace
            dto.setUnreadCount(0); // Fallback
            System.err.println("convertToDto: Set unread count to 0 for conversationId: " + dto.getConversationId() + " due to exception.");
        }
        
        System.out.println("convertToDto: Successfully converted conversation " + dto.getConversationId() + " to DTO. OtherUser: " + dto.getOtherUsername() + ", Unread: " + dto.getUnreadCount());
        return dto;
    }
    
    // 转换消息实体到DTO
    private MessageDto convertToDto(Message message) {
        if (message == null) {
            System.err.println("convertToDto(Message): Message object is null. Cannot convert to DTO.");
            throw new IllegalArgumentException("convertToDto(Message): Message object cannot be null.");
        }
        System.out.println("convertToDto(Message): Starting conversion for messageId: " + message.getMessageId());

        MessageDto dto = new MessageDto();
        dto.setMessageId(message.getMessageId());
        dto.setConversationId(message.getConversationId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setSendTime(message.getSendTime());
        dto.setIsRead(message.getIsRead());
        
        // 设置发送者和接收者信息
        try {
            if (userService == null) {
                System.err.println("convertToDto(Message): FATAL - userService is null! Cannot fetch sender/receiver details.");
                dto.setSenderName("用户" + message.getSenderId());
                dto.setSenderAvatar(null);
                dto.setReceiverName("用户" + message.getReceiverId());
                dto.setReceiverAvatar(null);
            } else {
                // 获取发送者信息
                System.out.println("convertToDto(Message): Attempting to get sender username for senderId: " + message.getSenderId());
                String senderName = userService.getUsernameById(message.getSenderId());
                dto.setSenderName(senderName != null ? senderName : "用户" + message.getSenderId());
                System.out.println("convertToDto(Message): Successfully got sender username: " + dto.getSenderName());

                System.out.println("convertToDto(Message): Attempting to get sender avatar for senderId: " + message.getSenderId());
                String senderAvatar = userService.getUserAvatarById(message.getSenderId());
                dto.setSenderAvatar(senderAvatar);
                System.out.println("convertToDto(Message): Successfully got sender avatar (null is acceptable): " + (senderAvatar != null ? senderAvatar : "null"));

                // 获取接收者信息
                System.out.println("convertToDto(Message): Attempting to get receiver username for receiverId: " + message.getReceiverId());
                String receiverName = userService.getUsernameById(message.getReceiverId());
                dto.setReceiverName(receiverName != null ? receiverName : "用户" + message.getReceiverId());
                System.out.println("convertToDto(Message): Successfully got receiver username: " + dto.getReceiverName());

                System.out.println("convertToDto(Message): Attempting to get receiver avatar for receiverId: " + message.getReceiverId());
                String receiverAvatar = userService.getUserAvatarById(message.getReceiverId());
                dto.setReceiverAvatar(receiverAvatar);
                System.out.println("convertToDto(Message): Successfully got receiver avatar (null is acceptable): " + (receiverAvatar != null ? receiverAvatar : "null"));
            }
        } catch (Exception e) {
            System.err.println("convertToDto(Message): Exception while fetching sender/receiver details for messageId: " + message.getMessageId() + ". Error Type: " + e.getClass().getName() + ", Error Message: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace
            // 设置默认值
            dto.setSenderName("用户" + message.getSenderId());
            dto.setSenderAvatar(null); // Default avatar
            dto.setReceiverName("用户" + message.getReceiverId());
            dto.setReceiverAvatar(null); // Default avatar
            System.err.println("convertToDto(Message): Set fallback sender/receiver details due to exception.");
        }
        
        System.out.println("convertToDto(Message): Successfully converted messageId: " + dto.getMessageId() + " to DTO. Sender: " + dto.getSenderName() + ", Receiver: " + dto.getReceiverName());
        return dto;
    }
}
