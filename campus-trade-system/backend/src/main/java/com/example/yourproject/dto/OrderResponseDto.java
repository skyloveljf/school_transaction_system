package com.example.yourproject.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.example.yourproject.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private OrderStatus status;
    private BigDecimal priceAtPurchase;
    private LocalDateTime orderTime;
    private LocalDateTime lastUpdateTime;
    private String productName;
    private String productImageUrl;
    private String buyerUsername;
    private String sellerUsername;
}