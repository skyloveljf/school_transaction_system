package com.example.yourproject.controller;

import com.example.yourproject.dto.ApiResponse;
import com.example.yourproject.dto.CommentCreateRequest;
import com.example.yourproject.dto.CommentDto;
import com.example.yourproject.service.CommentService;
import com.example.yourproject.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 获取商品的所有评论
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<CommentDto>>> getCommentsByProductId(
            @PathVariable Long productId) {
        try {
            List<CommentDto> comments = commentService.getCommentsByProductId(productId);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取评论成功", comments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "获取评论失败: " + e.getMessage(), null));
        }
    }

    /**
     * 添加新评论
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CommentDto>> addComment(
            @Valid @RequestBody CommentCreateRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户ID
            String tokenValue = token.replace("Bearer ", "");
            Integer userIdInt = jwtTokenUtil.getUserIdFromToken(tokenValue);
            
            if (userIdInt == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "无效的用户认证", null));
            }
            
            Long userId = userIdInt.longValue(); // 转换为Long类型
            CommentDto comment = commentService.addComment(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "评论发布成功", comment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "评论发布失败: " + e.getMessage(), null));
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户ID
            String tokenValue = token.replace("Bearer ", "");
            Integer userIdInt = jwtTokenUtil.getUserIdFromToken(tokenValue);
            
            if (userIdInt == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "无效的用户认证", null));
            }
            
            Long userId = userIdInt.longValue(); // 转换为Long类型
            boolean deleted = commentService.deleteComment(commentId, userId);
            
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse<>(true, "评论删除成功", null));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "无权删除该评论或评论不存在", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "评论删除失败: " + e.getMessage(), null));
        }
    }
} 