package com.example.yourproject.mapper;

import com.example.yourproject.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 消息数据访问接口
 */
@Mapper
public interface MessageMapper {
    
    /**
     * 根据会话ID查询消息列表，按发送时间排序
     */
    @Select("SELECT * FROM STS_DB.MESSAGES WHERE conversation_id = #{conversationId} ORDER BY send_time DESC LIMIT #{limit} OFFSET #{offset}")
    List<Message> findByConversationId(@Param("conversationId") Long conversationId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询用户收到的未读消息数量
     */
    @Select("SELECT COUNT(*) FROM STS_DB.MESSAGES WHERE receiver_id = #{receiverId} AND is_read = 0")
    int countUnreadByReceiverId(Long receiverId);
    
    /**
     * 查询指定会话中用户收到的未读消息数量
     */
    @Select("SELECT COUNT(*) FROM STS_DB.MESSAGES WHERE conversation_id = #{conversationId} AND receiver_id = #{receiverId} AND is_read = 0")
    int countUnreadByConversationAndReceiverId(@Param("conversationId") Long conversationId, @Param("receiverId") Long receiverId);
    
    /**
     * 插入新消息
     */
    @Insert("INSERT INTO STS_DB.MESSAGES (conversation_id, sender_id, receiver_id, content, send_time, is_read) " + 
           "VALUES (#{conversationId}, #{senderId}, #{receiverId}, #{content}, #{sendTime}, #{isRead})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId")
    int insert(Message message);
    
    /**
     * 标记消息为已读
     */
    @Update("UPDATE STS_DB.MESSAGES SET is_read = 1 WHERE message_id = #{messageId}")
    int markAsRead(Long messageId);
    
    /**
     * 标记会话中接收者的所有消息为已读
     */
    @Update("UPDATE STS_DB.MESSAGES SET is_read = 1 WHERE conversation_id = #{conversationId} AND receiver_id = #{receiverId}")
    int markAllAsRead(@Param("conversationId") Long conversationId, @Param("receiverId") Long receiverId);
} 