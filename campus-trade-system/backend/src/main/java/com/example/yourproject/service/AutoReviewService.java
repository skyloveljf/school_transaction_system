package com.example.yourproject.service;

import com.example.yourproject.dto.AutoReviewResultDto;
import java.util.List;
import java.util.Map;

/**
 * 商品自动审核服务接口
 */
public interface AutoReviewService {
    
    /**
     * 执行一键自动审核
     * 审核所有状态为PENDING_REVIEW的商品
     * @return 自动审核结果
     */
    AutoReviewResultDto executeAutoReview();
    
    /**
     * 审核单个商品
     * @param productId 商品ID
     * @return 审核结果（true=通过，false=拒绝）
     */
    boolean reviewSingleProduct(Long productId);
    
    /**
     * 获取自动审核历史记录
     * @return 审核历史记录列表
     */
    List<Map<String, Object>> getReviewHistory();
    
    /**
     * 清空自动审核历史记录
     * @return 是否成功清空
     */
    boolean clearReviewHistory();
    
    /**
     * 获取自动审核配置
     * @return 自动审核配置信息
     */
    Map<String, Object> getAutoReviewConfig();
    
    /**
     * 更新自动审核配置
     * @param enableAutoReview 是否启用自动审核
     * @param reviewBatchSize 批次审核数量
     * @param strictMode 是否启用严格模式
     * @return 是否更新成功
     */
    boolean updateAutoReviewConfig(Boolean enableAutoReview, Integer reviewBatchSize, Boolean strictMode);
}