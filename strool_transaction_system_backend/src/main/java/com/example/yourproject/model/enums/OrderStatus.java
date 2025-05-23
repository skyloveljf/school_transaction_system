package com.example.yourproject.model.enums;

public enum OrderStatus {
    PENDING,      // 待处理/待付款
    CONFIRMED,    // 已确认/待发货
    SHIPPED,      // 已发货
    COMPLETED,    // 已完成
    CANCELLED,    // 已取消
    REFUNDED      // 已退款 (可选)
}