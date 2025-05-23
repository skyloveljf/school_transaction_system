package com.example.yourproject.mapper;

import com.example.yourproject.model.Conversation;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 会话数据访问接口
 */
@Mapper
public interface ConversationMapper {
    
    /**
     * 根据会话ID查询会话
     */
    @Select("SELECT * FROM STS_DB.CONVERSATIONS WHERE conversation_id = #{conversationId}")
    Conversation findById(Long conversationId);
    
    /**
     * 查询用户参与的所有会话
     */
    @Select("SELECT * FROM STS_DB.CONVERSATIONS WHERE user1_id = #{userId} OR user2_id = #{userId} ORDER BY last_message_time DESC")
    List<Conversation> findByUserId(Long userId);
    
    /**
     * 查询两个用户之间的会话（如果存在）
     */
    @Select("SELECT * FROM STS_DB.CONVERSATIONS WHERE (user1_id = #{user1Id} AND user2_id = #{user2Id}) OR (user1_id = #{user2Id} AND user2_id = #{user1Id})")
    Conversation findByUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
    
    /**
     * 插入新会话
     */
    @Insert("INSERT INTO STS_DB.CONVERSATIONS (user1_id, user2_id, last_message_time) VALUES (#{user1Id}, #{user2Id}, #{lastMessageTime})")
    @Options(useGeneratedKeys = true, keyProperty = "conversationId")
    int insert(Conversation conversation);
    
    /**
     * 更新会话的最后消息时间
     */
    @Update("UPDATE STS_DB.CONVERSATIONS SET last_message_time = #{lastMessageTime} WHERE conversation_id = #{conversationId}")
    int updateLastMessageTime(@Param("conversationId") Long conversationId, @Param("lastMessageTime") java.time.LocalDateTime lastMessageTime);

    /**
     * (诊断方法) 查询两个用户之间所有的会话记录，用于排查重复数据问题
     */
    @Select("SELECT * FROM STS_DB.CONVERSATIONS WHERE (user1_id = #{user1Id} AND user2_id = #{user2Id}) OR (user1_id = #{user2Id} AND user2_id = #{user1Id})")
    List<Conversation> findAllByUsersForCleanup(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
} 