package com.example.yourproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long userId; // 对应 user_id INT IDENTITY(1,1) PRIMARY KEY
    private String username; // 对应 username VARCHAR(50) NOT NULL UNIQUE
    private String passwordHash; // 对应 password_hash VARCHAR(255) NOT NULL
    private String email; // 对应 email VARCHAR(100) NOT NULL UNIQUE
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registrationDate; // 对应 registration_date TIMESTAMP DEFAULT SYSTIMESTAMP
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastLoginDate; // 对应 last_login_date TIMESTAMP
    
    private String bio; // 用户简介
    private String nickname;
    private String avatarUrl; // 用户头像图片URL
    
    // 添加role字段，默认为"ROLE_USER"
    private String role = "ROLE_USER"; // 对应 role VARCHAR(50) DEFAULT 'ROLE_USER'
} 