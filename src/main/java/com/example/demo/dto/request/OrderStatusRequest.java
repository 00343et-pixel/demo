package com.example.demo.dto.request;

import com.example.demo.entity.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "update order status")
public record OrderStatusRequest(
    
    @NotNull
    @Schema(description = "order status", example = "SHIPPED")
    OrderStatus orderStatus
) {}
