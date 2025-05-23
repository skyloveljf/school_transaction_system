package com.example.yourproject.service;

import com.example.yourproject.model.Order;
import com.example.yourproject.model.User;
import java.util.List;

public interface OrderService {
    Order createOrder(Long productId, User buyer);
    Order getOrderById(Long orderId);
    List<Order> getOrdersByBuyerId(Long buyerId);
    List<Order> getOrdersBySellerId(Long sellerId);
    List<Order> getOrdersByProductId(Long productId);
    Order updateOrderStatus(Long orderId, String status, User currentUser); // 比如 "COMPLETED", "CANCELLED"
    // boolean cancelOrder(Long orderId, User currentUser); // 也可以细化取消操作
}