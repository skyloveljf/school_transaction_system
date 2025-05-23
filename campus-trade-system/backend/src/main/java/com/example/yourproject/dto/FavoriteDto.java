package com.example.yourproject.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FavoriteDto {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private String productImageUrl; // 商品图片URL (可选)
    private LocalDateTime favoriteTime;
}