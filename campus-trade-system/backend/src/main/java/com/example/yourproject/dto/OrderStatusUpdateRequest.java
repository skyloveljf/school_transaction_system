package com.example.yourproject.dto;
import com.example.yourproject.model.enums.OrderStatus;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; 

@Data
public class OrderStatusUpdateRequest {
    @NotBlank(message = "Status cannot be blank")
    private OrderStatus status;
}