package com.example.yourproject.controller;

import com.example.yourproject.dto.ApiResponse;
import com.example.yourproject.dto.ConversationDto;
import com.example.yourproject.dto.MessageDto;
import com.example.yourproject.service.ConversationService;
import com.example.yourproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会话控制器
 */
@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户的所有会话列表
     */
    @GetMapping
    public ApiResponse<List<ConversationDto>> getUserConversations() {
        // 从安全上下文获取当前用户ID
        Long currentUserId = getCurrentUserId();
        System.out.println("获取用户会话列表，用户ID: " + currentUserId);
        List<ConversationDto> conversations = conversationService.getUserConversations(currentUserId);
        return ApiResponse.success(conversations);
    }

    /**
     * 获取指定会话详情
     */
    @GetMapping("/{conversationId}")
    public ApiResponse<ConversationDto> getConversationDetail(@PathVariable Long conversationId) {
        // 从安全上下文获取当前用户ID
        Long currentUserId = getCurrentUserId();
        System.out.println("获取会话详情，会话ID: " + conversationId + "，用户ID: " + currentUserId);
        ConversationDto conversation = conversationService.getConversationDetail(conversationId, currentUserId);
        return ApiResponse.success(conversation);
    }

    /**
     * 创建新会话
     */
    @PostMapping
    public ApiResponse<ConversationDto> createConversation(@RequestBody Map<String, Object> params) {
        Long currentUserId = getCurrentUserId();
        
        // 打印完整请求体，帮助调试
        System.out.println("创建会话请求参数: " + params);
        
        // 尝试多种可能的参数格式
        Long receiverId = null;
        if (params.containsKey("receiverId")) {
            Object value = params.get("receiverId");
            if (value instanceof Number) {
                receiverId = ((Number) value).longValue();
            } else if (value instanceof String) {
                try {
                    receiverId = Long.parseLong((String) value);
                } catch (NumberFormatException e) {
                    return ApiResponse.<ConversationDto>error("接收者ID格式错误: " + value);
                }
            }
        }
        
        if (receiverId == null) {
            return ApiResponse.<ConversationDto>error("接收者ID不能为空");
        }
        
        System.out.println("创建会话，当前用户ID: " + currentUserId + "，接收者ID: " + receiverId);
        ConversationDto conversation = conversationService.createOrGetConversation(currentUserId, receiverId);
        return ApiResponse.success(conversation);
    }

    /**
     * 获取会话的消息列表
     */
    @GetMapping("/{conversationId}/messages")
    public ApiResponse<List<MessageDto>> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long currentUserId = getCurrentUserId();
        System.out.println("获取会话消息，会话ID: " + conversationId + 
                          "，用户ID: " + currentUserId + 
                          "，页码: " + page + 
                          "，大小: " + size);
        List<MessageDto> messages = conversationService.getConversationMessages(conversationId, currentUserId, page, size);
        return ApiResponse.success(messages);
    }

    /**
     * 在会话中发送消息
     */
    @PostMapping("/{conversationId}/messages")
    public ApiResponse<MessageDto> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody Map<String, Object> params) {
        Long currentUserId = getCurrentUserId();
        
        // 打印完整请求体，帮助调试
        System.out.println("发送消息请求参数: " + params + "，会话ID: " + conversationId);
        
        String content = null;
        if (params.containsKey("content")) {
            Object value = params.get("content");
            if (value instanceof String) {
                content = (String) value;
            } else {
                content = String.valueOf(value);
            }
        }
        
        if (content == null || content.trim().isEmpty()) {
            return ApiResponse.<MessageDto>error("消息内容不能为空");
        }
        
        System.out.println("发送消息，会话ID: " + conversationId + 
                          "，发送者ID: " + currentUserId + 
                          "，内容: " + content);
        MessageDto message = conversationService.sendMessage(conversationId, currentUserId, content);
        return ApiResponse.success(message);
    }

    /**
     * 标记会话为已读
     */
    @PutMapping("/{conversationId}/read")
    public ApiResponse<?> markConversationAsRead(@PathVariable Long conversationId) {
        Long currentUserId = getCurrentUserId();
        System.out.println("标记会话已读，会话ID: " + conversationId + "，用户ID: " + currentUserId);
        conversationService.markConversationAsRead(conversationId, currentUserId);
        return ApiResponse.successMessage("已标记为已读");
    }

    /**
     * 获取当前用户ID的方法，从Spring Security上下文获取
     */
    private Long getCurrentUserId() {
        try {
            // 从Spring Security获取当前登录用户ID
            org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            System.out.println("认证对象: " + (auth != null ? auth.toString() : "null"));
            
            if (auth != null && auth.isAuthenticated()) {
                System.out.println("认证类型: " + auth.getClass().getName());
                System.out.println("认证主体: " + (auth.getPrincipal() != null ? auth.getPrincipal().getClass().getName() : "null"));
                
                // 尝试从UserDetails中获取用户ID
                if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                    org.springframework.security.core.userdetails.UserDetails userDetails = 
                        (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();
                    
                    // 首先尝试将用户名转换为Long类型的ID
                    try {
                        return Long.parseLong(userDetails.getUsername());
                    } catch (NumberFormatException e) {
                        // 如果用户名不是数字，则尝试通过用户名查询用户ID
                        String username = userDetails.getUsername();
                        System.out.println("用户名不是数字，尝试通过用户名查询: " + username);
                        
                        // 通过UserService查询用户ID
                        com.example.yourproject.model.User user = userService.getUserByUsername(username);
                        if (user != null) {
                            Long userId = user.getUserId();
                            System.out.println("成功通过用户名查询到用户ID: " + userId);
                            return userId;
                        } else {
                            System.err.println("无法通过用户名查询到用户: " + username);
                        }
                    }
                } else {
                    // 尝试从其他位置获取用户ID
                    System.out.println("尝试从Authentication.getName()获取ID");
                    try {
                        String name = auth.getName();
                        // 首先尝试将名称解析为ID
                        try {
                            return Long.parseLong(name);
                        } catch (NumberFormatException e) {
                            // 名称不是数字，尝试查询
                            com.example.yourproject.model.User user = userService.getUserByUsername(name);
                            if (user != null) {
                                return user.getUserId();
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("无法从getName()获取用户ID: " + e.getMessage());
                    }
                }
            }
            
            // 如果无法从安全上下文获取，则记录警告
            System.out.println("警告: 无法从安全上下文获取用户ID，返回默认值");
        } catch (Exception e) {
            System.err.println("获取当前用户ID时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 为了调试，临时返回固定用户ID
        Long defaultUserId = 1L;
        System.out.println("使用默认用户ID: " + defaultUserId);
        return defaultUserId;
    }
}