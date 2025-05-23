package com.example.yourproject.service;

import com.example.yourproject.dto.ProductCreateDTO;
import com.example.yourproject.dto.ProductUpdateRequest;
import com.example.yourproject.model.Product;
import com.example.yourproject.model.User;
import com.example.yourproject.model.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    /**
     * 根据ID获取商品信息 (包含卖家和分类的详细信息)
     * 获取后会增加浏览次数
     * @param productId 商品ID
     * @return Product对象
     * @throws com.example.yourproject.exception.ResourceNotFoundException 如果未找到
     */
    Product getProductById(Long productId);

    /**
     * 获取所有商品列表 (包含卖家和分类的详细信息)
     * @param status 可选的商品状态，为null则获取所有状态（通常前端会指定如AVAILABLE）
     * @return 商品列表
     */
    List<Product> getAllProductsWithDetails(ProductStatus status);

    /**
     * 创建一个新商品
     * @param createRequest 包含商品信息的DTO
     * @param currentUser 当前登录用户 (用于设置发布者ID)
     * @return 创建后的Product对象（包含生成的ID和关联对象）
     * @throws com.example.yourproject.exception.BusinessRuleException 如果分类不存在或创建失败
     */
    Product createProduct(ProductCreateDTO createRequest, User currentUser);

    /**
     * 更新商品信息
     * @param productId 要更新的商品ID
     * @param updateRequest 包含待更新商品信息的DTO
     * @param currentUser 执行更新操作的用户ID (用于权限校验)
     * @return 更新后的Product对象
     * @throws com.example.yourproject.exception.ResourceNotFoundException 如果商品不存在
     * @throws com.example.yourproject.exception.UnauthorizedOperationException 如果用户无权更新
     * @throws com.example.yourproject.exception.BusinessRuleException 如果更新操作违反业务规则（如无效分类ID）
     */
    Product updateProduct(Long productId, ProductUpdateRequest updateRequest, User currentUser);

    /**
     * 删除商品
     * @param productId 商品ID
     * @param currentUser 执行删除操作的用户 (用于权限校验)
     * @throws com.example.yourproject.exception.ResourceNotFoundException 如果商品不存在
     * @throws com.example.yourproject.exception.UnauthorizedOperationException 如果用户无权删除
     * @throws com.example.yourproject.exception.BusinessRuleException 如果商品存在未完成订单
     */
    void deleteProduct(Long productId, User currentUser);

    /**
     * 根据用户ID获取该用户发布的商品列表 (包含详情)
     * @param userId 用户ID
     * @return 商品列表
     */
    List<Product> getProductsByUserId(Long userId);

    /**
     * 根据分类ID获取商品列表 (包含详情)
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> getProductsByCategoryId(Long categoryId);

    /**
     * 搜索商品 (基于标题、分类、价格范围等，包含详情)
     * @param keyword 搜索关键词 (可选)
     * @param categoryId 分类ID (可选)
     * @param minPrice 最低价格 (可选)
     * @param maxPrice 最高价格 (可选)
     * @param status 商品状态 (可选, 如 AVAILABLE)
     * @param fetchDetails 是否获取完整详情 (通常为 true)
     * @return 符合条件的商品列表
     */
    List<Product> searchProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, ProductStatus status, boolean fetchDetails);
}