package com.example.yourproject.controller;

import com.example.yourproject.dto.ApiResponse;
import com.example.yourproject.dto.UserDTO;
import com.example.yourproject.dto.CommentDto;
import com.example.yourproject.dto.AdminStatsDto;
import com.example.yourproject.dto.ProhibitedRuleDto;
import com.example.yourproject.dto.AutoReviewResultDto;
import com.example.yourproject.model.User;
import com.example.yourproject.model.Product;
import com.example.yourproject.service.UserService;
import com.example.yourproject.service.CommentService;
import com.example.yourproject.service.ProductService;
import com.example.yourproject.service.AdminStatsService;
import com.example.yourproject.service.ProhibitedRuleService;
import com.example.yourproject.service.AutoReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final CommentService commentService;
    private final ProductService productService;
    private final AdminStatsService adminStatsService;
    private final ProhibitedRuleService prohibitedRuleService;
    private final AutoReviewService autoReviewService;

    @Autowired
    public AdminController(UserService userService, CommentService commentService, 
                         ProductService productService, AdminStatsService adminStatsService,
                         ProhibitedRuleService prohibitedRuleService, AutoReviewService autoReviewService) {
        this.userService = userService;
        this.commentService = commentService;
        this.productService = productService;
        this.adminStatsService = adminStatsService;
        this.prohibitedRuleService = prohibitedRuleService;
        this.autoReviewService = autoReviewService;
    }

    /**
     * 获取当前管理员用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userService.getUserByUsername(currentPrincipalName);
        return currentUser != null ? currentUser.getUserId() : null;
    }

    /**
     * 获取系统状态信息
     */
    @GetMapping("/system-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("timestamp", System.currentTimeMillis());
        status.put("serverInfo", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        return ResponseEntity.ok(ApiResponse.success("系统正常运行中", status));
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        
        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setRegistrationDate(user.getRegistrationDate());
                    dto.setLastLoginDate(user.getLastLoginDate());
                    dto.setAvatarUrl(user.getAvatarUrl());
                    dto.setNickname(user.getNickname());
                    dto.setRole(user.getRole());
                    return dto;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("获取所有用户成功", userDTOs));
    }

    /**
     * 更改用户角色
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        
        // 检查角色格式，确保以ROLE_开头
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        
        User user = userService.updateUserRole(userId, role);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRegistrationDate(user.getRegistrationDate());
        userDTO.setLastLoginDate(user.getLastLoginDate());
        userDTO.setAvatarUrl(user.getAvatarUrl());
        userDTO.setNickname(user.getNickname());
        userDTO.setRole(user.getRole());
        
        return ResponseEntity.ok(ApiResponse.success("用户角色更新成功", userDTO));
    }

    /**
     * 获取普通用户列表（不包括管理员）
     */
    @GetMapping("/regular-users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getRegularUsers() {
        List<User> users = userService.getRegularUsers();
        
        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setRegistrationDate(user.getRegistrationDate());
                    dto.setLastLoginDate(user.getLastLoginDate());
                    dto.setAvatarUrl(user.getAvatarUrl());
                    dto.setNickname(user.getNickname());
                    return dto;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("获取普通用户列表成功", userDTOs));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
        try {
            boolean deleted = userService.deleteUser(userId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("用户删除成功", "用户ID: " + userId + " 已被删除"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("删除用户失败：未知错误"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除用户失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有评论（管理员功能）
     */
    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<List<CommentDto>>> getAllComments() {
        try {
            List<CommentDto> comments = commentService.getAllComments();
            return ResponseEntity.ok(ApiResponse.success("获取所有评论成功", comments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取评论失败：" + e.getMessage()));
        }
    }

    /**
     * 管理员删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId) {
        try {
            boolean deleted = commentService.adminDeleteComment(commentId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("评论删除成功", "评论ID: " + commentId + " 已被删除"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("删除评论失败：评论不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除评论失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有商品（管理员功能）
     */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProductsForAdmin();
            return ResponseEntity.ok(ApiResponse.success("获取所有商品成功", products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取商品失败：" + e.getMessage()));
        }
    }

    /**
     * 管理员审核商品
     */
    @PutMapping("/products/{productId}/review")
    public ResponseEntity<ApiResponse<String>> reviewProduct(
            @PathVariable Long productId,
            @RequestParam boolean approved) {
        try {
            boolean success = productService.reviewProduct(productId, approved);
            if (success) {
                String message = approved ? "商品审核通过" : "商品审核拒绝并已删除";
                return ResponseEntity.ok(ApiResponse.success(message, "商品ID: " + productId));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("审核商品失败：未知错误"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("审核商品失败：" + e.getMessage()));
        }
    }

    /**
     * 管理员删除商品（下架）
     */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long productId) {
        try {
            boolean deleted = productService.adminDeleteProduct(productId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("商品删除成功", "商品ID: " + productId + " 已被删除"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("删除商品失败：商品不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除商品失败：" + e.getMessage()));
        }
    }

    /**
     * 获取管理员统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsDto>> getAdminStats() {
        try {
            AdminStatsDto stats = adminStatsService.getAdminStats();
            return ResponseEntity.ok(ApiResponse.success("获取统计数据成功", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取统计数据失败：" + e.getMessage()));
        }
    }

    // ==================== 违禁规则管理 API ====================

    /**
     * 获取所有违禁规则
     */
    @GetMapping("/prohibited-rules")
    public ResponseEntity<ApiResponse<List<ProhibitedRuleDto>>> getAllProhibitedRules() {
        try {
            List<ProhibitedRuleDto> rules = prohibitedRuleService.getAllRules();
            return ResponseEntity.ok(ApiResponse.success("获取违禁规则成功", rules));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取违禁规则失败：" + e.getMessage()));
        }
    }

    /**
     * 获取激活的违禁规则
     */
    @GetMapping("/prohibited-rules/active")
    public ResponseEntity<ApiResponse<List<ProhibitedRuleDto>>> getActiveProhibitedRules() {
        try {
            List<ProhibitedRuleDto> rules = prohibitedRuleService.getActiveRules();
            return ResponseEntity.ok(ApiResponse.success("获取激活违禁规则成功", rules));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取激活违禁规则失败：" + e.getMessage()));
        }
    }

    /**
     * 创建新的违禁规则
     */
    @PostMapping("/prohibited-rules")
    public ResponseEntity<ApiResponse<ProhibitedRuleDto>> createProhibitedRule(
            @RequestParam String ruleName,
            @RequestParam String ruleDescription) {
        try {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("无法获取当前用户信息"));
            }
            
            ProhibitedRuleDto rule = prohibitedRuleService.createRule(ruleName, ruleDescription, currentUserId);
            return ResponseEntity.ok(ApiResponse.success("创建违禁规则成功", rule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建违禁规则失败：" + e.getMessage()));
        }
    }

    /**
     * 更新违禁规则
     */
    @PutMapping("/prohibited-rules/{ruleId}")
    public ResponseEntity<ApiResponse<ProhibitedRuleDto>> updateProhibitedRule(
            @PathVariable Long ruleId,
            @RequestParam String ruleName,
            @RequestParam String ruleDescription) {
        try {
            ProhibitedRuleDto rule = prohibitedRuleService.updateRule(ruleId, ruleName, ruleDescription);
            return ResponseEntity.ok(ApiResponse.success("更新违禁规则成功", rule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新违禁规则失败：" + e.getMessage()));
        }
    }

    /**
     * 删除违禁规则
     */
    @DeleteMapping("/prohibited-rules/{ruleId}")
    public ResponseEntity<ApiResponse<String>> deleteProhibitedRule(@PathVariable Long ruleId) {
        try {
            boolean deleted = prohibitedRuleService.deleteRule(ruleId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("删除违禁规则成功", "规则ID: " + ruleId + " 已被删除"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("删除违禁规则失败：规则不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除违禁规则失败：" + e.getMessage()));
        }
    }

    /**
     * 更新违禁规则状态
     */
    @PutMapping("/prohibited-rules/{ruleId}/status")
    public ResponseEntity<ApiResponse<String>> updateProhibitedRuleStatus(
            @PathVariable Long ruleId,
            @RequestParam Boolean isActive) {
        try {
            boolean updated = prohibitedRuleService.updateRuleStatus(ruleId, isActive);
            if (updated) {
                String status = isActive ? "激活" : "禁用";
                return ResponseEntity.ok(ApiResponse.success("更新规则状态成功", "规则ID: " + ruleId + " 已" + status));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("更新规则状态失败：规则不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新规则状态失败：" + e.getMessage()));
        }
    }

    // ==================== 自动审核 API ====================

    /**
     * 执行一键自动审核
     */
    @PostMapping("/products/auto-review")
    public ResponseEntity<ApiResponse<AutoReviewResultDto>> executeAutoReview() {
        try {
            AutoReviewResultDto result = autoReviewService.executeAutoReview();
            return ResponseEntity.ok(ApiResponse.success("自动审核完成", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("自动审核失败：" + e.getMessage()));
        }
    }

    /**
     * 审核单个商品（测试接口）
     */
    @PostMapping("/products/{productId}/auto-review")
    public ResponseEntity<ApiResponse<String>> reviewSingleProduct(@PathVariable Long productId) {
        try {
            boolean approved = autoReviewService.reviewSingleProduct(productId);
            String message = approved ? "商品自动审核通过" : "商品自动审核拒绝";
            return ResponseEntity.ok(ApiResponse.success(message, "商品ID: " + productId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("自动审核失败：" + e.getMessage()));
        }
    }

    /**
     * 获取自动审核历史记录
     */
    @GetMapping("/products/auto-review/history")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAutoReviewHistory() {
        try {
            List<Map<String, Object>> history = autoReviewService.getReviewHistory();
            return ResponseEntity.ok(ApiResponse.success("获取审核历史成功", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取审核历史失败：" + e.getMessage()));
        }
    }

    /**
     * 清空自动审核历史记录
     */
    @DeleteMapping("/products/auto-review/history")
    public ResponseEntity<ApiResponse<String>> clearAutoReviewHistory() {
        try {
            boolean cleared = autoReviewService.clearReviewHistory();
            if (cleared) {
                return ResponseEntity.ok(ApiResponse.success("清空审核历史成功", "所有审核历史记录已清空"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("清空审核历史失败：未知错误"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("清空审核历史失败：" + e.getMessage()));
        }
    }

    /**
     * 获取自动审核配置
     */
    @GetMapping("/products/auto-review/config")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAutoReviewConfig() {
        try {
            Map<String, Object> config = autoReviewService.getAutoReviewConfig();
            return ResponseEntity.ok(ApiResponse.success("获取自动审核配置成功", config));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取自动审核配置失败：" + e.getMessage()));
        }
    }

    /**
     * 更新自动审核配置
     */
    @PutMapping("/products/auto-review/config")
    public ResponseEntity<ApiResponse<String>> updateAutoReviewConfig(
            @RequestParam(required = false) Boolean enableAutoReview,
            @RequestParam(required = false) Integer reviewBatchSize,
            @RequestParam(required = false) Boolean strictMode) {
        try {
            boolean updated = autoReviewService.updateAutoReviewConfig(enableAutoReview, reviewBatchSize, strictMode);
            if (updated) {
                return ResponseEntity.ok(ApiResponse.success("更新自动审核配置成功", "配置已更新"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("更新自动审核配置失败：未知错误"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新自动审核配置失败：" + e.getMessage()));
        }
    }
}