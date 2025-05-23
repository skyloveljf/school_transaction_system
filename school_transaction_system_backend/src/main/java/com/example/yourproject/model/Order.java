package com.example.yourproject.model;

import com.example.yourproject.model.enums.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long orderId; // 对应 order_id INT IDENTITY(1,1) PRIMARY KEY
    private Long productId; // 对应 product_id INT NOT NULL
    private Long buyerId; // 对应 buyer_user_id INT NOT NULL
    private Long sellerId; // 对应 seller_user_id INT NOT NULL
    private OrderStatus status; // 使用枚举
    private BigDecimal priceAtPurchase; // 对应 order_price DECIMAL(10,2) NOT NULL
    private LocalDateTime orderTime; // 对应 created_at TIMESTAMP DEFAULT SYSTIMESTAMP
    private LocalDateTime lastUpdateTime; // 对应 updated_at TIMESTAMP DEFAULT SYSTIMESTAMP

    // 以下为关联对象，在MyBatis查询时可以填充
    private Product product; // 关联的商品信息
    private User buyer; // 关联的买家信息
    private User sellerInfo; // 关联的卖家信息 (通常与product.seller一致)
} 