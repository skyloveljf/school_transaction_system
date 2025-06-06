package com.example.yourproject.service;

import java.util.List;

/**
 * 通义千问视觉模型服务接口
 */
public interface QwenVisionService {
    
    /**
     * 检查商品图片与名称是否相符
     * @param imageUrl 商品图片URL
     * @param productName 商品名称
     * @return 是否相符（true=相符，false=不相符）
     */
    boolean checkImageNameMatch(String imageUrl, String productName);
    
    /**
     * 检查商品图片是否违反违禁规则
     * @param imageUrl 商品图片URL
     * @param prohibitedRules 违禁规则列表
     * @return 违规检查结果，返回null表示未违规，返回违规原因字符串表示违规
     */
    String checkImageViolation(String imageUrl, List<String> prohibitedRules);
    
    /**
     * 综合审核商品图片
     * @param imageUrl 商品图片URL
     * @param productName 商品名称
     * @param prohibitedRules 违禁规则列表
     * @return 审核结果对象
     */
    ReviewResult comprehensiveReview(String imageUrl, String productName, List<String> prohibitedRules);
    
    /**
     * 审核结果封装类
     */
    class ReviewResult {
        private boolean approved;           // 是否通过审核
        private String reason;             // 审核结果原因
        private boolean nameMatch;         // 图片与名称是否匹配
        private boolean hasViolation;      // 是否存在违规内容
        private String violationReason;    // 违规原因
        
        public ReviewResult() {}
        
        public ReviewResult(boolean approved, String reason, boolean nameMatch, boolean hasViolation, String violationReason) {
            this.approved = approved;
            this.reason = reason;
            this.nameMatch = nameMatch;
            this.hasViolation = hasViolation;
            this.violationReason = violationReason;
        }
        
        // Getters and Setters
        public boolean isApproved() {
            return approved;
        }
        
        public void setApproved(boolean approved) {
            this.approved = approved;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
        
        public boolean isNameMatch() {
            return nameMatch;
        }
        
        public void setNameMatch(boolean nameMatch) {
            this.nameMatch = nameMatch;
        }
        
        public boolean isHasViolation() {
            return hasViolation;
        }
        
        public void setHasViolation(boolean hasViolation) {
            this.hasViolation = hasViolation;
        }
        
        public String getViolationReason() {
            return violationReason;
        }
        
        public void setViolationReason(String violationReason) {
            this.violationReason = violationReason;
        }
        
        @Override
        public String toString() {
            return "ReviewResult{" +
                    "approved=" + approved +
                    ", reason='" + reason + '\'' +
                    ", nameMatch=" + nameMatch +
                    ", hasViolation=" + hasViolation +
                    ", violationReason='" + violationReason + '\'' +
                    '}';
        }
    }
}