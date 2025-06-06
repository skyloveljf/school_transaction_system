package com.example.yourproject.service;

import com.example.yourproject.dto.AdminStatsDto;

/**
 * 管理员统计服务接口
 */
public interface AdminStatsService {
    
    /**
     * 获取管理员统计数据
     * @return 包含所有统计信息的DTO
     */
    AdminStatsDto getAdminStats();
} 