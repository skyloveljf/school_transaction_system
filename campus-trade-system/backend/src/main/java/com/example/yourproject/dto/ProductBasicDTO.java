package com.example.yourproject.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductBasicDTO {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private UserDTO seller; //  或者只返回 sellerId 和 sellerNickname
    private CategoryDto category; // 或者只返回 categoryId 和 categoryName
    private LocalDateTime listedDate;
    private Integer views;
}
