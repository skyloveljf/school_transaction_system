package com.example.yourproject.service.impl;

import com.example.yourproject.service.QwenVisionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class QwenVisionServiceImpl implements QwenVisionService {
    
    private static final Logger logger = LoggerFactory.getLogger(QwenVisionServiceImpl.class);
    
    // 通义千问API配置
    private static final String QWEN_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String MODEL_NAME = "qwen-vl-max-2025-01-25";
    
    @Value("${qwen.api.key:sk-a68d5337a716418197c30bc997e52895}")
    private String qwenApiKey;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${storage.location:./uploads/images}")
    private String uploadPath;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public QwenVisionServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public boolean checkImageNameMatch(String imageUrl, String productName) {
        try {
            logger.info("检查图片与商品名称匹配性：{} - {}", imageUrl, productName);
            
            // 尝试使用Base64编码发送图片
            String imageData = convertImageToBase64(imageUrl);
            if (imageData != null) {
                logger.info("✅ 成功使用Base64编码发送图片: {}, 大小: {} 字符", imageUrl, imageData.length());
            } else {
                // 如果Base64转换失败，回退到URL方式
                imageData = convertToFullUrl(imageUrl);
                logger.warn("⚠️ Base64转换失败，回退到URL方式: {} -> {}", imageUrl, imageData);
            }
            
            String prompt = String.format("请观察这张图片，判断图片中的物品是否与商品名称\"%s\"基本一致或相关。" +
                    "只要图片中展示的主要物品与商品名称描述的类型大致匹配即可。" +
                    "请只回答\"相符\"或\"不相符\"，并简要说明理由。", productName);
            
            String response = callQwenVisionAPI(imageData, prompt);
            
            // 解析响应，判断是否相符
            boolean matches = response.contains("相符") && !response.contains("不相符");
            logger.info("图片名称匹配结果：{} - {}", matches, response);
            
            return matches;
            
        } catch (Exception e) {
            logger.error("检查图片名称匹配失败：{}", e.getMessage(), e);
            return false; // 出错时默认不匹配，拒绝审核
        }
    }
    
    @Override
    public String checkImageViolation(String imageUrl, List<String> prohibitedRules) {
        try {
            if (prohibitedRules == null || prohibitedRules.isEmpty()) {
                return null; // 没有违禁规则，直接通过
            }
            
            logger.info("检查图片违禁内容：{}", imageUrl);
            
            // 尝试使用Base64编码发送图片
            String imageData = convertImageToBase64(imageUrl);
            if (imageData != null) {
                logger.info("✅ 违禁检查使用Base64编码发送图片: {}", imageUrl);
            } else {
                // 如果Base64转换失败，回退到URL方式
                imageData = convertToFullUrl(imageUrl);
                logger.warn("⚠️ 违禁检查Base64转换失败，回退到URL方式: {} -> {}", imageUrl, imageData);
            }
            
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("请仔细检查这张图片是否违反以下规则：\n");
            for (int i = 0; i < prohibitedRules.size(); i++) {
                promptBuilder.append(String.format("%d. %s\n", i + 1, prohibitedRules.get(i)));
            }
            promptBuilder.append("\n如果图片违反了任何规则，请回答\"违规\"并说明违反了哪条规则；");
            promptBuilder.append("如果图片没有违反任何规则，请回答\"合规\"。");
            
            String response = callQwenVisionAPI(imageData, promptBuilder.toString());
            
            // 解析响应，判断是否违规
            if (response.contains("违规")) {
                logger.info("图片违禁检查结果：违规 - {}", response);
                return response; // 返回违规原因
            } else {
                logger.info("图片违禁检查结果：合规");
                return null; // 没有违规
            }
            
        } catch (Exception e) {
            logger.error("检查图片违禁内容失败：{}", e.getMessage(), e);
            return "系统检查异常，拒绝审核"; // 出错时默认拒绝
        }
    }
    
    @Override
    public ReviewResult comprehensiveReview(String imageUrl, String productName, List<String> prohibitedRules) {
        ReviewResult result = new ReviewResult();
        
        try {
            logger.info("开始综合审核商品：{} - {}", productName, imageUrl);
            
            // 1. 检查图片与名称是否匹配
            boolean nameMatch = checkImageNameMatch(imageUrl, productName);
            result.setNameMatch(nameMatch);
            
            // 2. 检查是否违反违禁规则
            String violationReason = checkImageViolation(imageUrl, prohibitedRules);
            boolean hasViolation = violationReason != null;
            result.setHasViolation(hasViolation);
            result.setViolationReason(violationReason);
            
            // 3. 综合判断审核结果
            if (!nameMatch) {
                result.setApproved(false);
                result.setReason("商品图片与名称不符");
            } else if (hasViolation) {
                result.setApproved(false);
                result.setReason("图片内容违反平台规定：" + violationReason);
            } else {
                result.setApproved(true);
                result.setReason("图片内容符合要求，审核通过");
            }
            
            logger.info("综合审核结果：{}", result);
            return result;
            
        } catch (Exception e) {
            logger.error("综合审核失败：{}", e.getMessage(), e);
            result.setApproved(false);
            result.setReason("系统审核异常：" + e.getMessage());
            return result;
        }
    }
    
    /**
     * 调用通义千问视觉API
     * @param imageData 图片数据，可以是URL或Base64编码
     * @param prompt 提示词
     */
    private String callQwenVisionAPI(String imageData, String prompt) throws Exception {
        // 检查API Key配置
        if ("your_qwen_api_key_here".equals(qwenApiKey)) {
            logger.warn("通义千问API Key未配置，返回模拟审核结果");
            return getMockResponse(prompt);
        }
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_NAME);
        
        // 构建消息内容
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        
        List<Map<String, Object>> content = new ArrayList<>();
        
        // 添加文本内容
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        content.add(textContent);
        
        // 添加图片内容
        Map<String, Object> imageContent = new HashMap<>();
        if (imageData.startsWith("data:image")) {
            // Base64格式
            imageContent.put("type", "image_url");
            Map<String, String> imageUrl_map = new HashMap<>();
            imageUrl_map.put("url", imageData);
            imageContent.put("image_url", imageUrl_map);
        } else {
            // URL格式
            imageContent.put("type", "image_url");
            Map<String, String> imageUrl_map = new HashMap<>();
            imageUrl_map.put("url", imageData);
            imageContent.put("image_url", imageUrl_map);
        }
        content.add(imageContent);
        
        message.put("content", content);
        requestBody.put("messages", List.of(message));
        
        // 设置其他参数
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 500);
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(qwenApiKey);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        logger.debug("调用通义千问API，请求体：{}", objectMapper.writeValueAsString(requestBody));
        
        // 发送请求
        ResponseEntity<String> response = restTemplate.exchange(
                QWEN_API_URL, 
                HttpMethod.POST, 
                entity, 
                String.class
        );
        
        logger.debug("通义千问API响应：{}", response.getBody());
        
        // 解析响应
        JsonNode responseNode = objectMapper.readTree(response.getBody());
        
        if (responseNode.has("choices") && responseNode.get("choices").isArray()) {
            JsonNode firstChoice = responseNode.get("choices").get(0);
            if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                String content_response = firstChoice.get("message").get("content").asText();
                logger.info("通义千问API返回内容：{}", content_response);
                return content_response;
            }
        }
        
        throw new RuntimeException("通义千问API响应格式异常");
    }
    
    /**
     * 将图片文件转换为Base64编码的数据URL
     */
    private String convertImageToBase64(String imagePath) {
        try {
            // 构建文件路径
            Path filePath;
            if (imagePath.startsWith("/uploads/images/")) {
                String fileName = imagePath.substring("/uploads/images/".length());
                filePath = Paths.get(uploadPath, fileName);
            } else {
                filePath = Paths.get(imagePath);
            }
            
            File imageFile = filePath.toFile();
            if (!imageFile.exists()) {
                logger.warn("图片文件不存在: {}", filePath);
                return null;
            }
            
            // 读取文件并转换为Base64
            try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
                byte[] fileBytes = fileInputStream.readAllBytes();
                String base64String = Base64.getEncoder().encodeToString(fileBytes);
                
                // 获取文件扩展名来确定MIME类型
                String fileName = imageFile.getName().toLowerCase();
                String mimeType;
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    mimeType = "image/jpeg";
                } else if (fileName.endsWith(".png")) {
                    mimeType = "image/png";
                } else if (fileName.endsWith(".gif")) {
                    mimeType = "image/gif";
                } else if (fileName.endsWith(".webp")) {
                    mimeType = "image/webp";
                } else {
                    mimeType = "image/jpeg"; // 默认为jpeg
                }
                
                String dataUrl = "data:" + mimeType + ";base64," + base64String;
                logger.debug("成功转换图片为Base64，大小: {} bytes", fileBytes.length);
                return dataUrl;
                
            }
        } catch (IOException e) {
            logger.error("转换图片为Base64失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 转换相对路径为完整的可访问URL
     */
    private String convertToFullUrl(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            throw new IllegalArgumentException("图片路径不能为空");
        }
        
        // 如果已经是完整URL，直接返回
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        
        // 构建完整URL
        String baseUrl = "http://localhost:" + serverPort;
        
        // 确保路径以/开头
        if (!imagePath.startsWith("/")) {
            imagePath = "/" + imagePath;
        }
        
        // 保持原有的/uploads/images路径，这个路径已在WebMvcConfig中配置
        // 不需要转换，直接使用原路径
        
        String fullUrl = baseUrl + imagePath;
        logger.debug("转换图片路径：{} -> {}", imagePath, fullUrl);
        
        return fullUrl;
    }
    
    /**
     * 获取模拟响应（当API Key未配置时使用）
     */
    private String getMockResponse(String prompt) {
        logger.info("生成模拟审核响应，提示：{}", prompt);
        
        if (prompt.contains("判断图片内容是否与商品名称")) {
            // 模拟名称匹配检查
            return "相符，图片内容与商品名称基本匹配";
        } else if (prompt.contains("检查这张图片是否违反以下规则")) {
            // 模拟违禁内容检查
            return "合规，图片内容未违反任何规则";
        } else {
            return "审核通过，内容符合要求";
        }
    }
}