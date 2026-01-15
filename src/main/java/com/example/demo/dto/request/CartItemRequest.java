package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "item request data")
public record CartItemRequest(
    
    @NotNull
    @Schema(description = "productId", example = "1")
    Long productId,

    @NotNull
    @Min(1)
    @Schema(description = "product quantity", example = "1")
    Integer quantity
) {}
