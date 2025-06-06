package com.example.yourproject.service.impl;

import com.example.yourproject.dto.AdminStatsDto;
import com.example.yourproject.service.AdminStatsService;
import com.example.yourproject.service.UserService;
import com.example.yourproject.service.ProductService;
import com.example.yourproject.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminStatsServiceImpl.class);
    
    private final UserService userService;
    private final ProductService productService;
    private final CommentService commentService;
    
    @Autowired
    public AdminStatsServiceImpl(UserService userService, ProductService productService, CommentService commentService) {
        this.userService = userService;
        this.productService = productService;
        this.commentService = commentService;
    }
    
    @Override
    public AdminStatsDto getAdminStats() {
        logger.info("开始获取管理员统计数据");
        
        try {
            // 获取基础统计数据
            Long totalUsers = userService.getTotalUserCount();
            Long totalProducts = productService.getAvailableProductCount();
            Long totalComments = commentService.getTotalCommentCount();
            
            logger.info("基础统计数据 - 用户总数: {}, 商品总数: {}, 评论总数: {}", 
                       totalUsers, totalProducts, totalComments);
            
            // 创建统计结果
            AdminStatsDto statsDto = new AdminStatsDto(totalUsers, totalProducts, totalComments);
            
            // 获取分类商品统计
            try {
                List<Map<String, Object>> categoryProductStats = productService.getCategoryProductStats();
                List<AdminStatsDto.CategoryStatsDto> categoryStats = new ArrayList<>();
                
                logger.info("获取到商品分类统计原始数据: {}", categoryProductStats);
                
                for (Map<String, Object> stat : categoryProductStats) {
                    // 尝试多种字段名格式（支持大小写）
                    String categoryName = (String) (stat.get("categoryName") != null ? stat.get("categoryName") : stat.get("CATEGORYNAME"));
                    Number productCountNum = (Number) (stat.get("productCount") != null ? stat.get("productCount") : stat.get("PRODUCTCOUNT"));
                    Number percentageNum = (Number) (stat.get("percentage") != null ? stat.get("percentage") : stat.get("PERCENTAGE"));
                    
                    Long productCount = productCountNum != null ? productCountNum.longValue() : 0L;
                    Double percentage = percentageNum != null ? percentageNum.doubleValue() : 0.0;
                    
                    if (categoryName != null) {
                        categoryStats.add(new AdminStatsDto.CategoryStatsDto(categoryName, productCount, percentage));
                        logger.info("添加商品分类统计: {} - 数量: {}, 占比: {}%", categoryName, productCount, percentage);
                    } else {
                        logger.warn("分类名称为空，跳过此条记录: {}", stat);
                    }
                }
                
                statsDto.setCategoryStats(categoryStats);
                logger.info("商品分类统计获取成功，共 {} 个分类", categoryStats.size());
            } catch (Exception e) {
                logger.error("获取商品分类统计失败", e);
                statsDto.setCategoryStats(new ArrayList<>());
            }
            
            // 获取评论分类统计
            try {
                List<Map<String, Object>> commentCategoryStats = commentService.getCommentCategoryStats();
                List<AdminStatsDto.CommentCategoryStatsDto> commentStats = new ArrayList<>();
                
                logger.info("获取到评论分类统计原始数据: {}", commentCategoryStats);
                
                for (Map<String, Object> stat : commentCategoryStats) {
                    // 尝试多种字段名格式（支持大小写）
                    String categoryName = (String) (stat.get("categoryName") != null ? stat.get("categoryName") : stat.get("CATEGORYNAME"));
                    Number commentCountNum = (Number) (stat.get("commentCount") != null ? stat.get("commentCount") : stat.get("COMMENTCOUNT"));
                    Number percentageNum = (Number) (stat.get("percentage") != null ? stat.get("percentage") : stat.get("PERCENTAGE"));
                    
                    Long commentCount = commentCountNum != null ? commentCountNum.longValue() : 0L;
                    Double percentage = percentageNum != null ? percentageNum.doubleValue() : 0.0;
                    
                    if (categoryName != null) {
                        commentStats.add(new AdminStatsDto.CommentCategoryStatsDto(categoryName, commentCount, percentage));
                        logger.info("添加评论分类统计: {} - 数量: {}, 占比: {}%", categoryName, commentCount, percentage);
                    } else {
                        logger.warn("分类名称为空，跳过此条记录: {}", stat);
                    }
                }
                
                statsDto.setCommentCategoryStats(commentStats);
                logger.info("评论分类统计获取成功，共 {} 个分类", commentStats.size());
            } catch (Exception e) {
                logger.error("获取评论分类统计失败", e);
                statsDto.setCommentCategoryStats(new ArrayList<>());
            }
            
            logger.info("管理员统计数据获取完成");
            return statsDto;
            
        } catch (Exception e) {
            logger.error("获取管理员统计数据失败", e);
            throw new RuntimeException("获取统计数据失败: " + e.getMessage());
        }
    }
}