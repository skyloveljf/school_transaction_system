package com.example.yourproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String tokenType = "Bearer";
    private String accessToken;
    private UserProfileDto user; // 包含一些用户信息返回给前端

    public AuthResponse(String accessToken, UserProfileDto user) {
        this.accessToken = accessToken;
        this.user = user;
    }
}