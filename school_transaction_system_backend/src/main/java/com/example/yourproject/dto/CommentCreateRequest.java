package com.example.yourproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentCreateRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    private Long parentId; // 如果是回复，则提供父评论ID

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
} 