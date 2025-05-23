package com.example.yourproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductCreateDTO {

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    private String productName;

    private String description;

    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须为正数")
    private BigDecimal price;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    // sellerId 将从认证信息中获取，不需要客户端传递

    @Size(max = 255, message = "图片URL长度不能超过255个字符")
    private String imageUrl; // 商品主图片URL

    // 如果有商品状态，可以在这里添加，例如 NEW, USED_LIKE_NEW 等
    // private String status; 
}
