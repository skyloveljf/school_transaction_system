package com.example.yourproject.service;

import com.example.yourproject.dto.CommentDto;
import com.example.yourproject.dto.CommentCreateRequest;

import java.util.List;

public interface CommentService {
    
    /**
     * 添加新评论
     * @param request 评论请求
     * @param userId 当前用户ID
     * @return 创建的评论DTO
     */
    CommentDto addComment(CommentCreateRequest request, Long userId);
    
    /**
     * 获取商品的所有评论（包括回复）
     * @param productId 商品ID
     * @return 评论列表（已组织成层级结构）
     */
    List<CommentDto> getCommentsByProductId(Long productId);
    
    /**
     * 删除评论（包括回复）
     * @param commentId 评论ID
     * @param userId 当前用户ID（用于权限检查）
     * @return 是否删除成功
     */
    boolean deleteComment(Long commentId, Long userId);

    /**
     * 获取所有评论（管理员功能）
     * @return 所有评论的列表，包含商品和用户信息
     */
    List<CommentDto> getAllComments();

    /**
     * 管理员删除评论（不需要权限检查）
     * @param commentId 评论ID
     * @return 是否删除成功
     */
    boolean adminDeleteComment(Long commentId);

    /**
     * 获取评论总数（管理员统计功能）
     * @return 评论总数
     */
    Long getTotalCommentCount();

    /**
     * 获取评论分类统计（管理员统计功能）
     * @return 评论分类和对应的评论数量列表
     */
    List<java.util.Map<String, Object>> getCommentCategoryStats();
} 