package com.example.yourproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponseDto {
    private Long productId;
    private String title;
    private String description;
    private BigDecimal price;
    private String status; // e.g., "AVAILABLE", "SOLD"
    private String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime postDate;
    private Integer views;

    private Long sellerId;
    private String sellerUsername;

    private Long categoryId;
    private String categoryName;
}