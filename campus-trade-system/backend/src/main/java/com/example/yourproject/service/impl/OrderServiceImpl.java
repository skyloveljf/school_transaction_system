package com.example.yourproject.service.impl;

import com.example.yourproject.model.Order;
import com.example.yourproject.model.Product;
import com.example.yourproject.model.User;
import com.example.yourproject.model.enums.OrderStatus; // 假设有一个订单状态枚举
import com.example.yourproject.model.enums.ProductStatus; // 假设有一个商品状态枚举
import com.example.yourproject.mapper.OrderMapper;
import com.example.yourproject.mapper.ProductMapper;
import com.example.yourproject.service.OrderService;
import com.example.yourproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    // private final ProductService productService; // 如果需要更复杂的商品状态更新逻辑

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public Order createOrder(Long productId, User buyer) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + productId);
        }
        if (product.getStatus() != ProductStatus.AVAILABLE) { // 假设定义了 ProductStatus 枚举
            throw new RuntimeException("Product is not available for purchase.");
        }
        if (product.getUserId().equals(buyer.getUserId())) {
            throw new RuntimeException("Seller cannot buy their own product.");
        }

        Order order = new Order();
        order.setProductId(productId);
        order.setBuyerId(buyer.getUserId());
        order.setSellerId(product.getUserId());
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING); // 假设定义了 OrderStatus 枚举
        order.setPriceAtPurchase(product.getPrice()); // 保存购买时的价格

        orderMapper.insert(order);

        // 更新商品状态为已售出或锁定 (可选逻辑)
        product.setStatus(ProductStatus.AVAILABLE); // 或 PENDING_PAYMENT 等
        productMapper.update(product);

        // 这里可以添加通知卖家等逻辑

        return orderMapper.findById(order.getOrderId()); // 返回包含完整信息的订单
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderMapper.findById(orderId);
    }

    @Override
    public List<Order> getOrdersByBuyerId(Long buyerId) {
        return orderMapper.findByBuyerId(buyerId);
    }

    @Override
    public List<Order> getOrdersBySellerId(Long sellerId) {
        return orderMapper.findBySellerId(sellerId);
    }

    @Override
    public List<Order> getOrdersByProductId(Long productId) {
        return orderMapper.findByProductId(productId);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, String status, User currentUser) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found with id: " + orderId);
        }

        // 权限校验：买家或卖家才能修改订单状态
        boolean isBuyer = order.getBuyerId().equals(currentUser.getUserId());
        boolean isSeller = order.getSellerId().equals(currentUser.getUserId());

        if (!isBuyer && !isSeller) {
            throw new RuntimeException("User not authorized to update this order's status.");
        }
        
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }


        // 状态流转逻辑 (简化版)
        // PENDING -> CONFIRMED (by seller) -> SHIPPED (by seller) -> COMPLETED (by buyer)
        // PENDING -> CANCELLED (by buyer or seller under certain conditions)
        // CONFIRMED -> CANCELLED (by buyer or seller under certain conditions)
        OrderStatus currentStatus = order.getStatus();

        if (isBuyer) {
            if (newStatus == OrderStatus.CANCELLED && (currentStatus == OrderStatus.PENDING)) {
                order.setStatus(newStatus);
                // 如果取消，可能需要将商品状态恢复为 AVAILABLE
                Product product = productMapper.findById(order.getProductId());
                if (product != null) {
                    product.setStatus(ProductStatus.AVAILABLE);
                    productMapper.update(product);
                }
            } else if (newStatus == OrderStatus.COMPLETED && currentStatus == OrderStatus.SHIPPED) {
                 order.setStatus(newStatus);
            } else {
                throw new RuntimeException("Buyer cannot change order status from " + currentStatus + " to " + newStatus);
            }
        } else { // isSeller
            if (newStatus == OrderStatus.CONFIRMED && currentStatus == OrderStatus.PENDING) {
                order.setStatus(newStatus);
            } else if (newStatus == OrderStatus.SHIPPED && currentStatus == OrderStatus.CONFIRMED) {
                order.setStatus(newStatus);
            } else if (newStatus == OrderStatus.CANCELLED && (currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.CONFIRMED)) {
                order.setStatus(newStatus);
                // 如果取消，可能需要将商品状态恢复为 AVAILABLE
                Product product = productMapper.findById(order.getProductId());
                if (product != null) {
                    product.setStatus(ProductStatus.AVAILABLE);
                    productMapper.update(product);
                }
            }
             else {
                throw new RuntimeException("Seller cannot change order status from " + currentStatus + " to " + newStatus);
            }
        }
        
        order.setLastUpdateTime(LocalDateTime.now());
        orderMapper.update(order);
        return order;
    }
}