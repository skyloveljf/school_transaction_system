package com.example.yourproject.controller;

import com.example.yourproject.dto.ApiResponse;
import com.example.yourproject.service.DeepSeekService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供AI能力的控制器，用于调用DeepSeek API
 */
@RestController
@RequestMapping("/api/ai")
public class DeepSeekController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekController.class);
    
    private final DeepSeekService deepSeekService;
    
    @Autowired
    public DeepSeekController(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }
    
    /**
     * 根据商品名称生成商品描述
     * @param productTitle 商品名称
     * @return 生成的商品描述
     */
    @PostMapping("/generate-description")
    public ResponseEntity<ApiResponse<String>> generateProductDescription(@RequestParam String productTitle) {
        logger.info("收到生成商品描述请求，商品名称: {}", productTitle);
        
        if (productTitle == null || productTitle.trim().isEmpty()) {
            logger.warn("商品名称为空，无法生成描述");
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, null, "商品名称不能为空"));
        }
        
        try {
            String description = deepSeekService.generateDescription(productTitle);
            
            if (description == null || description.trim().isEmpty()) {
                logger.warn("生成的描述为空，商品名称: {}", productTitle);
                return ResponseEntity.ok(
                    new ApiResponse<>(false, "无法生成描述，请稍后重试", null));
            }
            
            logger.info("成功生成商品描述，商品名称: {}, 描述长度: {}", productTitle, description.length());
            return ResponseEntity.ok(new ApiResponse<>(true, "成功生成商品描述", description));
            
        } catch (Exception e) {
            logger.error("生成商品描述时发生错误", e);
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>(false, "生成描述时发生错误: " + e.getMessage(), null));
        }
    }
} 