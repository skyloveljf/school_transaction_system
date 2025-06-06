package com.example.yourproject.mapper;

import com.example.yourproject.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {

    /**
     * 根据商品ID查询商品详细信息 (可能包含卖家和分类信息)
     * @param productId 商品ID
     * @return Product 对象，如果不存在则返回null
     */
    Product findById(@Param("productId") Long productId);

    /**
     * 插入一个新商品
     * @param product 商品对象
     * @return 影响的行数
     */
    int insert(Product product);

    /**
     * 更新商品信息
     * @param product 商品对象，必须包含productId
     * @return 影响的行数
     */
    int update(Product product);

    /**
     * 根据商品ID删除商品
     * @param productId 商品ID
     * @return 影响的行数
     */
    int deleteById(@Param("productId") Long productId);

    /**
     * 查询所有商品 (基础列表，可能不包含完整的关联对象，按发布日期降序)
     * @return 商品列表
     */
    List<Product> findAllBasic();

    /**
     * 查询所有商品，并包含卖家和分类信息 (按发布日期降序)
     * @return 包含详细信息的商品列表
     */
    List<Product> findAllWithDetails();
    /**
     * 根据商品状态查询所有商品，并包含卖家和分类信息 (按发布日期降序)
     * @param status 商品状态 (枚举的 name())
     * @return 包含详细信息的商品列表
     */
    List<Product> findAllWithDetailsByStatus(@Param("status") String status);
    /**
     * 根据提供的多个条件搜索商品详细信息。
     * @param keyword 搜索关键字 (匹配标题)
     * @param categoryId 分类ID
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param status 商品状态 (枚举的 name())
     * @return 符合条件的商品列表
     */
    List<Product> searchProducts(Map<String, Object> params);

    /**
     * 更新商品浏览次数
     * @param productId 商品ID
     * @return 影响的行数
     */
    int incrementViews(@Param("productId") Long productId);

    /**
     * 根据用户ID查询该用户发布的商品
     * @param userId 用户ID
     * @return 商品列表
     */
    List<Product> findByUserId(@Param("userId") Long userId);

    /**
     * 根据分类ID查询商品
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    List<Product> searchProductsDetailed(@Param("keyword") String keyword,
                                         @Param("categoryId") Long categoryId,
                                         @Param("minPrice") BigDecimal minPrice,
                                         @Param("maxPrice") BigDecimal maxPrice,
                                         @Param("status") String status);
    /**
     * 获取在售商品总数（统计功能）
     * @return 在售商品总数
     */
    Long countAvailableProducts();

    /**
     * 获取分类商品统计（统计功能）
     * @return 分类和对应的商品数量列表
     */
    List<Map<String, Object>> getCategoryProductStats();
}