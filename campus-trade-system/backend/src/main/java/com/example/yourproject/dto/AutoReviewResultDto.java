package com.example.yourproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoReviewResultDto {
    private int totalReviewed;           // 总审核数量
    private int approved;                // 通过数量
    private int rejected;                // 拒绝数量
    private int failed;                  // 审核失败数量
    private LocalDateTime reviewTime;    // 审核时间
    private List<ProductReviewDetail> details; // 审核详情列表
    
    public AutoReviewResultDto() {
        this.details = new ArrayList<>();
        this.reviewTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getTotalReviewed() {
        return totalReviewed;
    }
    
    public void setTotalReviewed(int totalReviewed) {
        this.totalReviewed = totalReviewed;
    }
    
    public int getApproved() {
        return approved;
    }
    
    public void setApproved(int approved) {
        this.approved = approved;
    }
    
    public int getRejected() {
        return rejected;
    }
    
    public void setRejected(int rejected) {
        this.rejected = rejected;
    }
    
    public int getFailed() {
        return failed;
    }
    
    public void setFailed(int failed) {
        this.failed = failed;
    }
    
    public LocalDateTime getReviewTime() {
        return reviewTime;
    }
    
    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }
    
    public List<ProductReviewDetail> getDetails() {
        return details;
    }
    
    public void setDetails(List<ProductReviewDetail> details) {
        this.details = details;
    }
    
    // 添加审核详情
    public void addDetail(ProductReviewDetail detail) {
        this.details.add(detail);
    }
    
    /**
     * 商品审核详情内部类
     */
    public static class ProductReviewDetail {
        private Long productId;
        private String productTitle;
        private boolean approved;
        private String reason;
        private String error;
        
        public ProductReviewDetail() {}
        
        public ProductReviewDetail(Long productId, String productTitle, boolean approved, String reason) {
            this.productId = productId;
            this.productTitle = productTitle;
            this.approved = approved;
            this.reason = reason;
        }
        
        public ProductReviewDetail(Long productId, String productTitle, String error) {
            this.productId = productId;
            this.productTitle = productTitle;
            this.approved = false;
            this.error = error;
        }
        
        // Getters and Setters
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public String getProductTitle() {
            return productTitle;
        }
        
        public void setProductTitle(String productTitle) {
            this.productTitle = productTitle;
        }
        
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
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
    }
    
    @Override
    public String toString() {
        return "AutoReviewResultDto{" +
                "totalReviewed=" + totalReviewed +
                ", approved=" + approved +
                ", rejected=" + rejected +
                ", failed=" + failed +
                ", reviewTime=" + reviewTime +
                ", details=" + details +
                '}';
    }
}