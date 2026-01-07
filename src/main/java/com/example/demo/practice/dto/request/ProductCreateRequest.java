package com.example.demo.practice.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "update order status")
public record ProductCreateRequest(
    
    @NotBlank
    @Schema(description = "product name", example = "iPhone17")
    String productName,

    @NotNull
    @Schema(description = "categoryId", example = "1")
    Long categoryId,

    @NotBlank
    @Schema(description = "description of product", example = "17th iPhone")
    String description,

    @NotNull
    @DecimalMin("0.00")
    @Schema(description = "price of product", example = "32000.00")
    BigDecimal price,

    @NotNull
    @Min(0)
    @Schema(description = "stock of product", example = "100")
    Integer stock
) {}
