package com.example.yourproject.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoginDate;
    private String nickname;
    private String avatarUrl;
} 