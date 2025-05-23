package com.example.yourproject.model;

import com.example.yourproject.model.enums.ProductStatus; // 引入枚举
import com.fasterxml.jackson.annotation.JsonFormat; // 添加导入
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable; // 添加Serializable接口导入
import java.math.BigDecimal;
import java.time.LocalDateTime;
// import java.util.List; // 如果需要直接持有订单列表，则取消注释

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable { // 实现Serializable接口
    private static final long serialVersionUID = 1L; // 添加序列化版本ID

    private Long productId; // 对应 product_id INT IDENTITY(1,1) PRIMARY KEY
    private Long userId; // 对应 user_id INT NOT NULL (发布者ID)
    private Long categoryId; // 对应 category_id INT NOT NULL
    private String title; // 对应 title VARCHAR(200) NOT NULL
    private String description; // 对应 description CLOB
    private BigDecimal price; // 对应 price DECIMAL(10,2) NOT NULL
    private ProductStatus status; // 对应 status VARCHAR(20) DEFAULT 'AVAILABLE'
    private String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") // 添加注解
    private LocalDateTime postDate; // 对应 post_date TIMESTAMP DEFAULT SYSTIMESTAMP
    private Integer views; // 对应 views INT DEFAULT 0

    // 以下为关联对象，在MyBatis查询时可以填充
    private User seller; // 关联的卖家信息 (通过userId)
    private Category category; // 关联的分类信息 (通过categoryId)

    // 如果需要直接在Product中包含订单列表 (一对多关系)
    // private List<Order> orders;
} 