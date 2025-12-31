package com.example.demo.practice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "create category data")
public record CategoryCreateRequest(
    
    @NotBlank
    @Schema(description = "category name", example = "electronics")
    String categoryName
) {}
