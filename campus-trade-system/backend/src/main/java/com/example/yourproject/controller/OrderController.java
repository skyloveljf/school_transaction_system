package com.example.yourproject.controller;

import com.example.yourproject.model.Order;
import com.example.yourproject.model.User;
import com.example.yourproject.service.OrderService;
import com.example.yourproject.service.UserService;
import com.example.yourproject.dto.OrderResponseDto; // 需要创建
import com.example.yourproject.dto.OrderCreateRequest; // 需要创建
import com.example.yourproject.dto.OrderStatusUpdateRequest; // 需要创建

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserByUsername(authentication.getName());
    }

    private OrderResponseDto convertToDto(Order order) {
        if (order == null) return null;
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setProductId(order.getProductId());
        dto.setBuyerId(order.getBuyerId());
        dto.setSellerId(order.getSellerId());
        dto.setStatus(order.getStatus());
        dto.setPriceAtPurchase(order.getPriceAtPurchase());
        dto.setOrderTime(order.getOrderTime());
        dto.setLastUpdateTime(order.getLastUpdateTime());

        if (order.getProduct() != null) {
            dto.setProductName(order.getProduct().getTitle()); // Assuming Product has getTitle()
            dto.setProductImageUrl(order.getProduct().getImageUrl());
        }
        if (order.getBuyer() != null) {
            dto.setBuyerUsername(order.getBuyer().getUsername());
        }
        if (order.getSellerInfo() != null){ // Assuming 'sellerInfo' is the field name in Order model for seller user
            dto.setSellerUsername(order.getSellerInfo().getUsername());
        } else if (order.getProduct() != null && order.getProduct().getSeller() != null) {
            // Fallback if sellerInfo is not populated directly in Order, but through Product
            dto.setSellerUsername(order.getProduct().getSeller().getUsername());
        }
        return dto;
    }

    // POST /api/orders - 创建订单 (需要认证)
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderCreateRequest createRequest) {
        User currentUser = getCurrentUser();
        Order order = orderService.createOrder(createRequest.getProductId(), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(order));
    }

    // GET /api/orders/{id} - 获取订单详情 (需要认证，买家或卖家)
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        // 权限校验：只有买家或卖家能查看自己的订单详情
        if (!order.getBuyerId().equals(currentUser.getUserId()) && !order.getSellerId().equals(currentUser.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(convertToDto(order));
    }

    // GET /api/orders/my-purchases - 获取当前用户作为买家的订单 (需要认证)
    @GetMapping("/my-purchases")
    public ResponseEntity<List<OrderResponseDto>> getMyPurchases() {
        User currentUser = getCurrentUser();
        List<Order> orders = orderService.getOrdersByBuyerId(currentUser.getUserId());
        List<OrderResponseDto> dtos = orders.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/orders/my-sales - 获取当前用户作为卖家的订单 (需要认证)
    @GetMapping("/my-sales")
    public ResponseEntity<List<OrderResponseDto>> getMySales() {
        User currentUser = getCurrentUser();
        List<Order> orders = orderService.getOrdersBySellerId(currentUser.getUserId());
        List<OrderResponseDto> dtos = orders.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    // GET /api/orders/product/{productId} - 获取某商品的所有订单 (通常应限制给管理员或相关方)
    // 暂不实现，如需此功能，请注意权限控制

    // PATCH /api/orders/{id}/status - 更新订单状态 (需要认证，买家或卖家根据权限)
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest statusUpdateRequest) {
        User currentUser = getCurrentUser();
        Order updatedOrder = orderService.updateOrderStatus(id, statusUpdateRequest.getStatus().name(), currentUser);
        if (updatedOrder == null) {
            // 通常 service 层会抛异常，这里简单处理
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Or more specific error
        }
        return ResponseEntity.ok(convertToDto(updatedOrder));
    }
}