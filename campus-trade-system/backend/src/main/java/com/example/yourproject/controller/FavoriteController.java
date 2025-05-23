package com.example.yourproject.controller;

import com.example.yourproject.model.Favorite;
import com.example.yourproject.model.User;
import com.example.yourproject.service.FavoriteService;
import com.example.yourproject.service.UserService;
import com.example.yourproject.dto.FavoriteDto;
import com.example.yourproject.dto.FavoriteRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService, UserService userService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserByUsername(authentication.getName());
    }

    // GET /api/favorites/my - 获取当前用户的收藏列表
    @GetMapping("/my")
    public ResponseEntity<List<FavoriteDto>> getMyFavorites() {
        User currentUser = getCurrentUser();
        List<FavoriteDto> favorites = favoriteService.getFavoritesByUserId(currentUser.getUserId());
        return ResponseEntity.ok(favorites);
    }

    // POST /api/favorites - 添加商品到收藏
    @PostMapping
    public ResponseEntity<?> addFavorite(@Valid @RequestBody FavoriteRequest favoriteRequest) {
        try {
            User currentUser = getCurrentUser();
            Favorite favorite = favoriteService.addFavorite(favoriteRequest.getProductId(), currentUser);
            
            // 转换为DTO返回
            FavoriteDto responseDto = new FavoriteDto();
            responseDto.setId(favorite.getFavoriteId());
            responseDto.setUserId(favorite.getUserId());
            responseDto.setProductId(favorite.getProductId());
            responseDto.setFavoriteTime(favorite.getFavoriteTime());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // DELETE /api/favorites/{favoriteId} - 取消收藏
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long favoriteId) {
        try {
            User currentUser = getCurrentUser();
            favoriteService.removeFavorite(favoriteId, currentUser);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "收藏已成功取消");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // GET /api/favorites/product/{productId}/status - 检查商品是否已收藏
    @GetMapping("/product/{productId}/status")
    public ResponseEntity<Boolean> isProductFavorited(@PathVariable Long productId) {
        User currentUser = getCurrentUser();
        boolean isFavorited = favoriteService.isProductFavoritedByUser(productId, currentUser.getUserId());
        return ResponseEntity.ok(isFavorited);
    }

    // DELETE /api/favorites/product/{productId} - 通过商品ID取消收藏
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> removeFavoriteByProductId(@PathVariable Long productId) {
        try {
            User currentUser = getCurrentUser();
            // 查找用户的这个收藏项
            boolean isFavorited = favoriteService.isProductFavoritedByUser(productId, currentUser.getUserId());
            if (!isFavorited) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "您未收藏此商品");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            // 调用service方法删除收藏
            favoriteService.removeFavoriteByProductId(productId, currentUser);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "收藏已成功取消");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}