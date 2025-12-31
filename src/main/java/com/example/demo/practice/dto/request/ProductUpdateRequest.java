package com.example.demo.practice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "update product data")
public record ProductUpdateRequest(

    @Schema(description = "product name", example = "iPad Air4")
    String name,
    
    @Schema(description = "categoryId", example = "1")
    Long categoryId,

    @Schema(description = "description of product", example = "iPad")
    String description,

    @Min(0)
    @Schema(description = "price of product", example = "23000")
    Integer price,

    @Min(0)
    @Schema(description = "stock of product", example = "200")
    Integer stock,

    @Schema(description = "available", example = "ture")
    Boolean isActive
) {}
