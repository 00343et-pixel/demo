package com.example.demo.practice.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

@Schema(description = "update product data")
public record ProductUpdateRequest(

    @Schema(description = "product name", example = "iPad Air4")
    String productName,
    
    @Schema(description = "categoryId", example = "1")
    Long categoryId,

    @Schema(description = "description of product", example = "iPad")
    String description,

    @DecimalMin("0.00")
    @Schema(description = "price of product", example = "23000.00")
    BigDecimal price,

    @Min(0)
    @Schema(description = "stock of product", example = "200")
    Integer stock,

    @Schema(description = "available", example = "ture")
    Boolean isActive
) {}
