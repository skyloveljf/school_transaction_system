package com.example.yourproject.service;

import com.example.yourproject.model.Favorite;
import com.example.yourproject.model.User;
import com.example.yourproject.dto.FavoriteDto; // 假设有一个包含商品详情的DTO
import java.util.List;

public interface FavoriteService {
    Favorite addFavorite(Long productId, User user);
    void removeFavorite(Long favoriteId, User user); // 或者 (Long productId, User user)
    List<FavoriteDto> getFavoritesByUserId(Long userId);
    boolean isProductFavoritedByUser(Long productId, Long userId);
    long countFavoritesByProductId(Long productId);
    void removeFavoriteByProductId(Long productId, User user); // 添加通过商品ID取消收藏的方法
}