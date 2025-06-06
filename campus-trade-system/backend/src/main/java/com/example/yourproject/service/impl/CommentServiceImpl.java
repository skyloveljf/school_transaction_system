package com.example.yourproject.service.impl;

import com.example.yourproject.dto.CommentDto;
import com.example.yourproject.dto.CommentCreateRequest;
import com.example.yourproject.mapper.CommentMapper;
import com.example.yourproject.mapper.UserMapper;
import com.example.yourproject.mapper.ProductMapper;
import com.example.yourproject.model.Comment;
import com.example.yourproject.model.Product;
import com.example.yourproject.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional
    public CommentDto addComment(CommentCreateRequest request, Long userId) {
        // 创建新评论
        Comment comment = new Comment();
        comment.setProductId(request.getProductId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setCreatedTime(LocalDateTime.now());
        comment.setParentId(request.getParentId());
        
        // 保存评论
        commentMapper.addComment(comment);
        
        // 获取用户信息
        String username = userMapper.getUsernameById(userId);
        
        // 构建返回DTO
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setProductId(comment.getProductId());
        dto.setUserId(comment.getUserId());
        dto.setUsername(username);
        dto.setContent(comment.getContent());
        dto.setCreatedTime(comment.getCreatedTime());
        dto.setParentId(comment.getParentId());
        dto.setReplies(new ArrayList<>());
        
        return dto;
    }
    
    @Override
    public List<CommentDto> getCommentsByProductId(Long productId) {
        // 获取所有顶级评论
        List<Comment> topLevelComments = commentMapper.getTopLevelCommentsByProductId(productId);
        List<CommentDto> result = new ArrayList<>();
        
        // 用于缓存用户名的Map，避免重复查询
        Map<Long, String> userNameCache = new HashMap<>();
        
        // 转换顶级评论并获取回复
        for (Comment comment : topLevelComments) {
            CommentDto dto = convertToDto(comment, userNameCache);
            
            // 获取该评论的所有回复
            List<Comment> replies = commentMapper.getRepliesByParentId(comment.getId());
            List<CommentDto> replyDtos = new ArrayList<>();
            
            // 转换回复评论
            for (Comment reply : replies) {
                replyDtos.add(convertToDto(reply, userNameCache));
            }
            
            dto.setReplies(replyDtos);
            result.add(dto);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        // 获取评论
        Comment comment = commentMapper.getCommentById(commentId);
        
        // 检查评论是否存在
        if (comment == null) {
            return false;
        }
        
        // 检查是否有权限删除（评论所有者或管理员）
        if (!comment.getUserId().equals(userId)) {
            // 这里可以添加管理员检查逻辑
            return false;
        }
        
        // 删除评论及其回复
        return commentMapper.deleteComment(commentId) > 0;
    }
    
    @Override
    public List<CommentDto> getAllComments() {
        // 获取所有评论
        List<Comment> comments = commentMapper.getAllComments();
        List<CommentDto> result = new ArrayList<>();
        
        // 用于缓存用户名和商品标题的Map，避免重复查询
        Map<Long, String> userNameCache = new HashMap<>();
        Map<Long, String> productTitleCache = new HashMap<>();
        
        // 转换评论
        for (Comment comment : comments) {
            CommentDto dto = convertToAdminDto(comment, userNameCache, productTitleCache);
            result.add(dto);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean adminDeleteComment(Long commentId) {
        // 管理员删除评论，不需要权限检查
        return commentMapper.deleteComment(commentId) > 0;
    }
    
    /**
     * 将Comment实体转换为CommentDto
     */
    private CommentDto convertToDto(Comment comment, Map<Long, String> userNameCache) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setProductId(comment.getProductId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setCreatedTime(comment.getCreatedTime());
        dto.setParentId(comment.getParentId());
        
        // 获取用户名，优先从缓存获取
        if (!userNameCache.containsKey(comment.getUserId())) {
            String username = userMapper.getUsernameById(comment.getUserId());
            userNameCache.put(comment.getUserId(), username);
        }
        dto.setUsername(userNameCache.get(comment.getUserId()));
        
        return dto;
    }
    
    /**
     * 将Comment实体转换为管理员使用的CommentDto（包含商品信息）
     */
    private CommentDto convertToAdminDto(Comment comment, Map<Long, String> userNameCache, Map<Long, String> productTitleCache) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setProductId(comment.getProductId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setCreatedTime(comment.getCreatedTime());
        dto.setParentId(comment.getParentId());
        
        // 获取用户名，优先从缓存获取
        if (!userNameCache.containsKey(comment.getUserId())) {
            String username = userMapper.getUsernameById(comment.getUserId());
            userNameCache.put(comment.getUserId(), username);
        }
        dto.setUsername(userNameCache.get(comment.getUserId()));
        
        // 获取商品标题，优先从缓存获取
        if (!productTitleCache.containsKey(comment.getProductId())) {
            Product product = productMapper.findById(comment.getProductId());
            String productTitle = product != null ? product.getTitle() : "商品已删除";
            productTitleCache.put(comment.getProductId(), productTitle);
        }
        dto.setProductTitle(productTitleCache.get(comment.getProductId()));
        
        return dto;
    }

    @Override
    public Long getTotalCommentCount() {
        return commentMapper.countAllComments();
    }

    @Override
    public List<java.util.Map<String, Object>> getCommentCategoryStats() {
        return commentMapper.getCommentCategoryStats();
    }
} 