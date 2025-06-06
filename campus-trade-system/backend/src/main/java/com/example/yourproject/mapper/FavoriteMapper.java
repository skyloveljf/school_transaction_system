package com.example.yourproject.mapper;

import com.example.yourproject.model.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    /**
     * 根据收藏ID查询收藏记录 (包含关联对象)
     * @param favoriteId 收藏ID
     * @return Favorite 对象，如果不存在则返回null
     */
    Favorite findById(@Param("favoriteId") Long favoriteId); // Added this method

    /**
     * 根据用户ID和商品ID查询收藏记录
     * @param userId 用户ID
     * @param productId 商品ID
     * @return Favorite 对象，如果不存在则返回null
     */
    Favorite findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 插入一条新的收藏记录
     * @param favorite 收藏对象
     * @return 影响的行数
     */
    int insert(Favorite favorite);

    /**
     * 根据用户ID和商品ID删除收藏记录
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 影响的行数
     */
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 根据收藏ID删除收藏记录
     * @param favoriteId 收藏ID
     * @return 影响的行数
     */
    int deleteById(@Param("favoriteId") Long favoriteId);

    /**
     * 根据用户ID查询其所有收藏的商品列表 (包含商品详情)
     * @param userId 用户ID
     * @return 收藏列表，每个Favorite对象包含关联的Product信息
     */
    List<Favorite> findFavoritesByUserIdWithProductDetails(@Param("userId") Long userId); // Renamed from findFavoritesByUserId

    /**
     * 根据商品ID查询收藏了该商品的用户数量
     * @param productId 商品ID
     * @return 收藏数量 (XML中定义的返回类型是long, Mapper接口中是int，需要统一)
     */
    long countFavoritesByProductId(@Param("productId") Long productId); // Changed return type to long to match XML

    /**
     * 根据商品ID删除所有相关收藏记录
     * @param productId 商品ID
     * @return 删除的收藏记录数量
     */
    int deleteFavoritesByProductId(@Param("productId") Long productId);
}
