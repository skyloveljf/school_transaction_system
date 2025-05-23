package com.example.yourproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long categoryId; // 对应 category_id INT IDENTITY(1,1) PRIMARY KEY
    private String categoryName; // 对应 category_name VARCHAR(100) NOT NULL UNIQUE
    private String description; // 对应 description CLOB
} 