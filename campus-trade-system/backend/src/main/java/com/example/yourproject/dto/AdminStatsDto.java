package com.example.yourproject.dto;

import java.util.List;
import java.util.Map;

/**
 * 管理员统计数据DTO
 */
public class AdminStatsDto {
    
    private Long totalUsers;           // 用户总数
    private Long totalProducts;        // 在售商品总数
    private Long totalComments;        // 评论总数
    private List<CategoryStatsDto> categoryStats;      // 分类统计
    private List<CommentCategoryStatsDto> commentCategoryStats; // 评论分类统计
    
    public AdminStatsDto() {}
    
    public AdminStatsDto(Long totalUsers, Long totalProducts, Long totalComments) {
        this.totalUsers = totalUsers;
        this.totalProducts = totalProducts;
        this.totalComments = totalComments;
    }
    
    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public Long getTotalProducts() {
        return totalProducts;
    }
    
    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }
    
    public Long getTotalComments() {
        return totalComments;
    }
    
    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }
    
    public List<CategoryStatsDto> getCategoryStats() {
        return categoryStats;
    }
    
    public void setCategoryStats(List<CategoryStatsDto> categoryStats) {
        this.categoryStats = categoryStats;
    }
    
    public List<CommentCategoryStatsDto> getCommentCategoryStats() {
        return commentCategoryStats;
    }
    
    public void setCommentCategoryStats(List<CommentCategoryStatsDto> commentCategoryStats) {
        this.commentCategoryStats = commentCategoryStats;
    }
    
    /**
     * 商品分类统计DTO
     */
    public static class CategoryStatsDto {
        private String categoryName;
        private Long productCount;
        private Double percentage;
        
        public CategoryStatsDto() {}
        
        public CategoryStatsDto(String categoryName, Long productCount, Double percentage) {
            this.categoryName = categoryName;
            this.productCount = productCount;
            this.percentage = percentage;
        }
        
        public String getCategoryName() {
            return categoryName;
        }
        
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
        
        public Long getProductCount() {
            return productCount;
        }
        
        public void setProductCount(Long productCount) {
            this.productCount = productCount;
        }
        
        public Double getPercentage() {
            return percentage;
        }
        
        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }
    
    /**
     * 评论分类统计DTO
     */
    public static class CommentCategoryStatsDto {
        private String categoryName;
        private Long commentCount;
        private Double percentage;
        
        public CommentCategoryStatsDto() {}
        
        public CommentCategoryStatsDto(String categoryName, Long commentCount, Double percentage) {
            this.categoryName = categoryName;
            this.commentCount = commentCount;
            this.percentage = percentage;
        }
        
        public String getCategoryName() {
            return categoryName;
        }
        
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
        
        public Long getCommentCount() {
            return commentCount;
        }
        
        public void setCommentCount(Long commentCount) {
            this.commentCount = commentCount;
        }
        
        public Double getPercentage() {
            return percentage;
        }
        
        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }
}