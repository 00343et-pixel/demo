package com.example.demo.practice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "update item quantity")
public record CartUpdateRequest(

    @NotNull
    @Min(1)
    @Schema(description = "item quantity", example = "10")
    Integer quantity
) {}