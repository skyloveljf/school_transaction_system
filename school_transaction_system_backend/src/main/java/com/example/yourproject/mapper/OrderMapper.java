package com.example.yourproject.mapper;

import com.example.yourproject.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 根据订单ID查询订单详情
     * @param orderId 订单ID
     * @return Order 对象，可能包含关联的 Product, Buyer, Seller 信息
     */
    Order findById(@Param("orderId") Long orderId);

    /**
     * 插入一个新订单
     * @param order 订单对象
     * @return 影响的行数
     */
    int insert(Order order);

    /**
     * 更新订单信息 (主要用于更新订单状态)
     * @param order 订单对象，必须包含orderId
     * @return 影响的行数
     */
    int update(Order order);

    /**
     * 根据买家ID查询其所有订单
     * @param buyerUserId 买家用户ID
     * @return 订单列表
     */
    List<Order> findByBuyerId(@Param("buyerUserId") Long buyerUserId);

    /**
     * 根据卖家ID查询其所有售出订单
     * @param sellerUserId 卖家用户ID
     * @return 订单列表
     */
    List<Order> findBySellerId(@Param("sellerUserId") Long sellerUserId);

    /**
     * 根据商品ID查询相关的所有订单
     * @param productId 商品ID
     * @return 订单列表
     */
    List<Order> findByProductId(@Param("productId") Long productId);
    
    /**
     * (可选) 根据多个条件查询订单，例如状态、日期范围等，可用于后台管理
     * @param params 包含查询条件的Map
     * @return 订单列表
     */
    // List<Order> searchOrders(Map<String, Object> params);
} 