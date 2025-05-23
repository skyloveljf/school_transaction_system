package com.example.yourproject.service;

import com.example.yourproject.dto.ConversationDto;
import com.example.yourproject.dto.MessageDto;

import java.util.List;

/**
 * 会话服务接口
 */
public interface ConversationService {

    /**
     * 获取用户的所有会话列表
     * @param userId 用户ID
     * @return 会话DTO列表
     */
    List<ConversationDto> getUserConversations(Long userId);

    /**
     * 获取指定会话详情
     * @param conversationId 会话ID
     * @param userId 当前用户ID
     * @return 会话DTO
     */
    ConversationDto getConversationDetail(Long conversationId, Long userId);

    /**
     * 创建或获取与指定用户的会话
     * @param currentUserId 当前用户ID
     * @param receiverId 接收者用户ID
     * @return 会话DTO
     */
    ConversationDto createOrGetConversation(Long currentUserId, Long receiverId);

    /**
     * 获取会话的消息列表
     * @param conversationId 会话ID
     * @param userId 当前用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 消息DTO列表
     */
    List<MessageDto> getConversationMessages(Long conversationId, Long userId, Integer page, Integer size);

    /**
     * 发送消息
     * @param conversationId 会话ID
     * @param senderId 发送者ID
     * @param content 消息内容
     * @return 消息DTO
     */
    MessageDto sendMessage(Long conversationId, Long senderId, String content);

    /**
     * 标记会话为已读
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    void markConversationAsRead(Long conversationId, Long userId);
}