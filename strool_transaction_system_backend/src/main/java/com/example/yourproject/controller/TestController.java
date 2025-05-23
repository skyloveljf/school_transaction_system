package com.example.yourproject.controller;

import com.example.yourproject.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器，用于验证API连接性
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 简单的 ping-pong 测试
     */
    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success("pong");
    }

    /**
     * 回显提供的参数
     */
    @PostMapping("/echo")
    public ApiResponse<Map<String, Object>> echo(@RequestBody(required = false) Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        
        if (params != null) {
            response.putAll(params);
        }
        
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "success");
        
        return ApiResponse.success(response);
    }

    /**
     * 用于测试当前身份
     */
    @GetMapping("/whoami")
    public ApiResponse<Map<String, Object>> whoAmI() {
        Map<String, Object> userInfo = new HashMap<>();
        
        try {
            org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            userInfo.put("isAuthenticated", auth != null && auth.isAuthenticated());
            userInfo.put("principal", auth != null ? auth.getPrincipal().toString() : "none");
            userInfo.put("authorities", auth != null ? auth.getAuthorities().toString() : "none");
            userInfo.put("details", auth != null ? auth.getDetails() : "none");
        } catch (Exception e) {
            userInfo.put("error", e.getMessage());
        }
        
        return ApiResponse.success(userInfo);
    }
} 