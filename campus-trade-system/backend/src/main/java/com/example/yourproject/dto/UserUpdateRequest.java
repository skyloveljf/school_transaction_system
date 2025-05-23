package com.example.yourproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    // 用户名通常不允许修改，或有特殊流程
    // @Size(min = 3, max = 50, message = "用户名长度必须在3到50个字符之间")
    // private String username;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email; // 只允许更新邮箱示例
    
    @Size(max = 500, message = "简介长度不能超过500个字符")
    private String bio; // 用户简介
    
    private String avatarUrl; // 用户头像URL

    // 如果允许更新密码，需要单独的DTO和逻辑，例如 OldPassword, NewPassword
}