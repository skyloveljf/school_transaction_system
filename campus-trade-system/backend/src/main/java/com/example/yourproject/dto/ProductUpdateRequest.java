package com.example.yourproject.dto;

import com.example.yourproject.model.enums.ProductStatus; // 如果允许更新状态
import lombok.Data;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {

    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title; // 可选更新

    private String description; // 可选更新

    @Positive(message = "价格必须为正数")
    private BigDecimal price; // 可选更新

    private Long categoryId; // 可选更新
    @Size(max = 255, message = "图片URL长度不能超过255个字符")
    private String imageUrl; // 可选更新
    
    private ProductStatus status; // 可选更新，例如管理员或卖家可以下架商品
}