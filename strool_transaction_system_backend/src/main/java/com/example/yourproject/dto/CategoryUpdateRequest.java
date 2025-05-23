package com.example.yourproject.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
public class CategoryUpdateRequest {

    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String categoryName; // 可选更新

    private String description; // 可选更新
}