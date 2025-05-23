package com.example.yourproject.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class OrderCreateRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    // buyerId 将从认证信息中获取
    // price 和 sellerId 将从商品信息中获取
    // orderDate 将在服务端设置
    // status 初始状态也将在服务端设置
}
 