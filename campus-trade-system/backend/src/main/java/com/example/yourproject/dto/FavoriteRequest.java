package com.example.yourproject.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class FavoriteRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
}