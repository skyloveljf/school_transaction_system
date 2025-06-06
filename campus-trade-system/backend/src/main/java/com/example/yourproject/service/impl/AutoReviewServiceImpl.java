package com.example.yourproject.service.impl;

import com.example.yourproject.dto.AutoReviewResultDto;
import com.example.yourproject.model.Product;
import com.example.yourproject.model.enums.ProductStatus;
import com.example.yourproject.service.AutoReviewService;
import com.example.yourproject.service.ProductService;
import com.example.yourproject.service.ProhibitedRuleService;
import com.example.yourproject.service.QwenVisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class AutoReviewServiceImpl implements AutoReviewService {
    
    private static final Logger logger = LoggerFactory.getLogger(AutoReviewServiceImpl.class);
    
    private final ProductService productService;
    private final ProhibitedRuleService prohibitedRuleService;
    private final QwenVisionService qwenVisionService;
    
    // 线程池用于并发处理多个商品审核
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    @Autowired
    public AutoReviewServiceImpl(ProductService productService, 
                               ProhibitedRuleService prohibitedRuleService,
                               QwenVisionService qwenVisionService) {
        this.productService = productService;
        this.prohibitedRuleService = prohibitedRuleService;
        this.qwenVisionService = qwenVisionService;
    }
    
    @Override
    @Transactional
    public AutoReviewResultDto executeAutoReview() {
        logger.info("开始执行一键自动审核");
        
        AutoReviewResultDto result = new AutoReviewResultDto();
        result.setReviewTime(LocalDateTime.now());
        
        try {
            // 1. 获取所有待审核的商品
            List<Product> pendingProducts = productService.getAllProductsWithDetails(ProductStatus.PENDING_REVIEW);
            
            if (pendingProducts.isEmpty()) {
                logger.info("没有待审核的商品");
                return result;
            }
            
            result.setTotalReviewed(pendingProducts.size());
            logger.info("找到 {} 个待审核商品", pendingProducts.size());
            
            // 2. 获取当前激活的违禁规则
            List<String> prohibitedRules = prohibitedRuleService.getActiveRuleDescriptions();
            logger.info("获取到 {} 条违禁规则", prohibitedRules.size());
            
            // 3. 并发审核所有商品
            List<CompletableFuture<AutoReviewResultDto.ProductReviewDetail>> futures = 
                    pendingProducts.stream()
                            .map(product -> CompletableFuture.supplyAsync(() -> 
                                    reviewProductInternal(product, prohibitedRules), executorService))
                            .toList();
            
            // 4. 等待所有审核完成（最多等待5分钟）
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));
            
            try {
                allFutures.get(5, TimeUnit.MINUTES);
            } catch (Exception e) {
                logger.warn("部分商品审核超时", e);
            }
            
            // 5. 统计审核结果
            int approved = 0, rejected = 0, failed = 0;
            
            for (CompletableFuture<AutoReviewResultDto.ProductReviewDetail> future : futures) {
                try {
                    AutoReviewResultDto.ProductReviewDetail detail = future.get();
                    result.addDetail(detail);
                    
                    if (detail.getError() != null) {
                        failed++;
                    } else if (detail.isApproved()) {
                        approved++;
                    } else {
                        rejected++;
                    }
                } catch (Exception e) {
                    logger.error("获取审核结果失败", e);
                    failed++;
                }
            }
            
            result.setApproved(approved);
            result.setRejected(rejected);
            result.setFailed(failed);
            
            logger.info("自动审核完成：总数={}, 通过={}, 拒绝={}, 失败={}", 
                    result.getTotalReviewed(), approved, rejected, failed);
            
            return result;
            
        } catch (Exception e) {
            logger.error("执行自动审核失败", e);
            throw new RuntimeException("自动审核失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean reviewSingleProduct(Long productId) {
        try {
            logger.info("开始审核单个商品，ID：{}", productId);
            
            // 获取商品信息
            Product product = productService.getProductById(productId);
            if (product == null) {
                logger.error("商品不存在，ID：{}", productId);
                return false;
            }
            
            if (product.getStatus() != ProductStatus.PENDING_REVIEW) {
                logger.error("商品状态不是待审核，ID：{}，状态：{}", productId, product.getStatus());
                return false;
            }
            
            // 获取违禁规则
            List<String> prohibitedRules = prohibitedRuleService.getActiveRuleDescriptions();
            
            // 执行审核
            AutoReviewResultDto.ProductReviewDetail detail = reviewProductInternal(product, prohibitedRules);
            
            logger.info("单个商品审核完成，ID：{}，结果：{}", productId, detail.isApproved());
            return detail.isApproved();
            
        } catch (Exception e) {
            logger.error("审核单个商品失败，ID：{}", productId, e);
            return false;
        }
    }
    
    /**
     * 内部审核方法
     */
    private AutoReviewResultDto.ProductReviewDetail reviewProductInternal(Product product, List<String> prohibitedRules) {
        AutoReviewResultDto.ProductReviewDetail detail = 
                new AutoReviewResultDto.ProductReviewDetail();
        detail.setProductId(product.getProductId());
        detail.setProductTitle(product.getTitle());
        
        try {
            logger.info("开始审核商品：{} - {}", product.getProductId(), product.getTitle());
            
            // 检查商品图片URL是否有效
            if (product.getImageUrl() == null || product.getImageUrl().trim().isEmpty()) {
                detail.setApproved(false);
                detail.setReason("商品图片缺失");
                // 执行拒绝操作
                productService.reviewProduct(product.getProductId(), false);
                return detail;
            }
            
            // 使用通义千问进行综合审核
            QwenVisionService.ReviewResult reviewResult = qwenVisionService.comprehensiveReview(
                    product.getImageUrl(), 
                    product.getTitle(), 
                    prohibitedRules
            );
            
            detail.setApproved(reviewResult.isApproved());
            detail.setReason(reviewResult.getReason());
            
            // 执行审核操作（通过或拒绝）
            boolean success = productService.reviewProduct(product.getProductId(), reviewResult.isApproved());
            
            if (!success) {
                detail.setError("执行审核操作失败");
            }
            
            logger.info("商品审核完成：ID={}, 标题={}, 结果={}, 原因={}", 
                    product.getProductId(), product.getTitle(), reviewResult.isApproved(), reviewResult.getReason());
            
        } catch (Exception e) {
            logger.error("审核商品失败：ID={}, 标题={}", product.getProductId(), product.getTitle(), e);
            detail.setApproved(false);
            detail.setError("审核异常：" + e.getMessage());
        }
        
        return detail;
    }
    
    /**
     * 关闭线程池
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
    
    @Override
    public List<Map<String, Object>> getReviewHistory() {
        // 这里可以返回一个简单的历史记录，实际项目中可以连接数据库存储审核历史
        List<Map<String, Object>> history = new ArrayList<>();
        
        // 示例历史记录
        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("reviewTime", LocalDateTime.now().minusHours(1));
        historyEntry.put("totalReviewed", 10);
        historyEntry.put("approved", 8);
        historyEntry.put("rejected", 2);
        historyEntry.put("reviewType", "自动审核");
        
        history.add(historyEntry);
        
        return history;
    }
    
    @Override
    public boolean clearReviewHistory() {
        // 实际项目中应该清空数据库中的审核历史记录
        logger.info("清空自动审核历史记录");
        return true;
    }
    
    @Override
    public Map<String, Object> getAutoReviewConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("enableAutoReview", true);
        config.put("reviewBatchSize", 50);
        config.put("strictMode", false);
        config.put("maxConcurrency", 5);
        config.put("timeoutMinutes", 5);
        return config;
    }
    
    @Override
    public boolean updateAutoReviewConfig(Boolean enableAutoReview, Integer reviewBatchSize, Boolean strictMode) {
        logger.info("更新自动审核配置：enableAutoReview={}, reviewBatchSize={}, strictMode={}", 
                enableAutoReview, reviewBatchSize, strictMode);
        
        // 实际项目中应该将配置保存到数据库或配置文件
        // 这里只是简单的日志记录
        
        if (enableAutoReview != null) {
            logger.info("设置自动审核开关：{}", enableAutoReview);
        }
        
        if (reviewBatchSize != null && reviewBatchSize > 0) {
            logger.info("设置批次审核数量：{}", reviewBatchSize);
        }
        
        if (strictMode != null) {
            logger.info("设置严格模式：{}", strictMode);
        }
        
        return true;
    }
}