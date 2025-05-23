package com.example.yourproject.controller;

import com.example.yourproject.dto.UserProfileDto;
import com.example.yourproject.dto.UserUpdateRequest;
import com.example.yourproject.dto.ChangePasswordRequest;
import com.example.yourproject.model.User;
import com.example.yourproject.service.UserService;
import com.example.yourproject.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;


    @Autowired
    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // 获取当前登录用户的个人资料
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body("用户未认证");
        }
        
        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("用户未找到");
        }
        return ResponseEntity.ok(UserProfileDto.fromUser(user));
    }

    // 更新当前登录用户的个人资料
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUserProfile(@Valid @RequestBody UserUpdateRequest updateRequest) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body("用户未认证");
        }
        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return ResponseEntity.status(404).body("用户数据异常");
        }

        // 准备更新User对象
        User userToUpdate = new User();
        userToUpdate.setUserId(currentUser.getUserId());
        userToUpdate.setEmail(updateRequest.getEmail()); // 更新邮箱
        userToUpdate.setBio(updateRequest.getBio()); // 更新简介
        userToUpdate.setAvatarUrl(updateRequest.getAvatarUrl()); // 更新头像URL

        try {
            User updatedUser = userService.updateUserProfile(userToUpdate);
            return ResponseEntity.ok(UserProfileDto.fromUser(updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 修改当前登录用户的密码
    @PostMapping("/me/change-password")
    public ResponseEntity<?> changeCurrentUserPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body("用户未认证");
        }
        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return ResponseEntity.status(404).body("用户数据异常");
        }

        try {
            boolean success = userService.changePassword(
                currentUser.getUserId(),
                changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword(),
                changePasswordRequest.getConfirmNewPassword()
            );
            if (success) {
                return ResponseEntity.ok("密码修改成功");
            } else {
                // 通常service层会抛出异常，这里作为后备
                return ResponseEntity.badRequest().body("密码修改失败，未知原因");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}