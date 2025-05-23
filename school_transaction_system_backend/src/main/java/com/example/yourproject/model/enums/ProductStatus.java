package com.example.yourproject.model.enums;

public enum ProductStatus {
    AVAILABLE,    // 可购买
    PENDING_PAYMENT, // 待付款 (被下单但未付款)
    SOLD,         // 已售出
    RESERVED,     // 已预订 (其他场景可能用到)
    UNAVAILABLE   // 不可购买/下架
}