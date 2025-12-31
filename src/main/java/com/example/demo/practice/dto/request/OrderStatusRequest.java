package com.example.demo.practice.dto.request;

import com.example.demo.practice.entity.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "update order status")
public record OrderStatusRequest(
    
    @NotNull
    @Schema(description = "order status", example = "SHIPPED")
    OrderStatus orderStatus
) {}
