package com.example.yourproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long userId;
    private String username;
    private String email;
    private String nickname; // 新增
    private String avatarUrl; // 新增
    private String bio; // 添加简介字段
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoginDate;

    // 可以从 User 实体类转换而来
    public static UserProfileDto fromUser(com.example.yourproject.model.User user) {
        if (user == null) return null;
        return new UserProfileDto(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getNickname(),
            user.getAvatarUrl(),
            user.getBio(),
            user.getRegistrationDate(),
            user.getLastLoginDate()
        );
    }
}