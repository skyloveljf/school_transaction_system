package com.example.yourproject.mapper;

import com.example.yourproject.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    
    /**
     * 添加新评论
     */
    int addComment(Comment comment);
    
    /**
     * 获取商品的所有顶级评论（不包括回复）
     */
    List<Comment> getTopLevelCommentsByProductId(@Param("productId") Long productId);
    
    /**
     * 获取评论的所有回复
     */
    List<Comment> getRepliesByParentId(@Param("parentId") Long parentId);
    
    /**
     * 获取单个评论
     */
    Comment getCommentById(@Param("id") Long id);
    
    /**
     * 删除评论（包括该评论的所有回复）
     */
    int deleteComment(@Param("id") Long id);
    
    /**
     * 获取评论总数
     */
    int getCommentCountByProductId(@Param("productId") Long productId);

    /**
     * 获取所有评论（管理员功能）
     */
    List<Comment> getAllComments();

    /**
     * 获取评论总数（统计功能）
     */
    Long countAllComments();

    /**
     * 获取评论分类统计（统计功能）
     */
    List<java.util.Map<String, Object>> getCommentCategoryStats();

    /**
     * 根据商品ID删除所有相关评论（包括回复）
     * @param productId 商品ID
     * @return 删除的评论数量
     */
    int deleteCommentsByProductId(@Param("productId") Long productId);
} 