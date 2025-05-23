package com.example.yourproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long favoriteId; // 对应 favorite_id INT IDENTITY(1,1) PRIMARY KEY
    private Long userId; // 对应 user_id INT NOT NULL
    private Long productId; // 对应 product_id INT NOT NULL
    private LocalDateTime favoriteTime; // 对应 created_at TIMESTAMP DEFAULT SYSTIMESTAMP

    // 以下为关联对象，在MyBatis查询时可以填充
    private User user; // 关联的用户信息
    private Product product; // 关联的商品信息
} 