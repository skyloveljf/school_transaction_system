package com.example.yourproject.service.impl;

import com.example.yourproject.dto.FavoriteDto;
import com.example.yourproject.mapper.FavoriteMapper;
import com.example.yourproject.mapper.ProductMapper;
import com.example.yourproject.model.Favorite;
import com.example.yourproject.model.Product;
import com.example.yourproject.model.User;
import com.example.yourproject.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper; // 用于校验商品是否存在

    @Autowired
    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, ProductMapper productMapper) {
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public Favorite addFavorite(Long productId, User user) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + productId);
        }

        if (favoriteMapper.findByUserIdAndProductId(user.getUserId(), productId) != null) {
            throw new RuntimeException("Product already favorited by this user.");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(user.getUserId());
        favorite.setProductId(productId);
        favorite.setFavoriteTime(LocalDateTime.now());

        favoriteMapper.insert(favorite);
        return favorite; // 通常返回刚创建的实体，包含ID
    }

    @Override
    @Transactional
    public void removeFavorite(Long favoriteId, User user) {
        Favorite favorite = favoriteMapper.findById(favoriteId);
        if (favorite == null) {
            throw new RuntimeException("Favorite entry not found with id: " + favoriteId);
        }
        if (!favorite.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("User not authorized to remove this favorite entry.");
        }
        favoriteMapper.deleteById(favoriteId);
    }

    @Override
    @Transactional
    public void removeFavoriteByProductId(Long productId, User user) {
        Favorite favorite = favoriteMapper.findByUserIdAndProductId(user.getUserId(), productId);
        if (favorite == null) {
            throw new RuntimeException("收藏项未找到");
        }
        favoriteMapper.deleteById(favorite.getFavoriteId());
    }

    @Override
    public List<FavoriteDto> getFavoritesByUserId(Long userId) {
        // 获取包含Product详细信息的收藏列表
        List<Favorite> favorites = favoriteMapper.findFavoritesByUserIdWithProductDetails(userId);
        
        // 如果收藏为空，返回空列表
        if (favorites == null || favorites.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        return favorites.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private FavoriteDto convertToDto(Favorite favorite) {
        if (favorite == null) {
            return null; // 防止空指针
        }
        
        FavoriteDto dto = new FavoriteDto();
        dto.setId(favorite.getFavoriteId());
        dto.setUserId(favorite.getUserId());
        dto.setProductId(favorite.getProductId());
        dto.setFavoriteTime(favorite.getFavoriteTime());
        
        // 如果关联的Product存在，就填充相关信息；否则设为默认值或null
        if (favorite.getProduct() != null) {
            dto.setProductName(favorite.getProduct().getTitle());
            dto.setProductPrice(favorite.getProduct().getPrice());
            dto.setProductImageUrl(favorite.getProduct().getImageUrl());
        } else {
            // 当Product为null时，尝试直接获取该商品信息
            Product product = productMapper.findById(favorite.getProductId());
            if (product != null) {
                dto.setProductName(product.getTitle());
                dto.setProductPrice(product.getPrice());
                dto.setProductImageUrl(product.getImageUrl());
            } else {
                // 如果仍然无法获取，设置默认值
                dto.setProductName("未知商品");
                dto.setProductPrice(null);
                dto.setProductImageUrl(null);
            }
        }
        
        return dto;
    }


    @Override
    public boolean isProductFavoritedByUser(Long productId, Long userId) {
        return favoriteMapper.findByUserIdAndProductId(userId, productId) != null;
    }

    @Override
    public long countFavoritesByProductId(Long productId) {
        return favoriteMapper.countFavoritesByProductId(productId);
    }
}