package com.example.demo.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

import com.example.demo.entity.Product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "product imformation")
public class ProductResponse implements Serializable {

    @Schema(description = "product ID", example = "1")
    Long productId;

    @Schema(description = "product name", example = "iPhone17")
    String productName;

    @Schema(description = "category ID", example = "1")
    Long categoryId;

    @Schema(description = "description of product", example = "17th iPhone")
    String description;

    @Schema(description = "price", example = "32000.00")
    BigDecimal price;

    @Schema(description = "stock", example = "50")
    Integer stock;

    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getCategory().getId(),
            product.getDescription(),
            product.getPrice(),
            product.getStock()
        );
    }
}
