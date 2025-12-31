package com.example.demo.practice.dto.response;

import java.io.Serializable;

import com.example.demo.practice.entity.CartItem;
import com.example.demo.practice.entity.OrderItem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "item imformation")
public class ItemResponse implements Serializable {
    
    @Schema(description = "product name", example = "iPhone")
    private String productName;

    @Schema(description = "quantity of product", example = "5")
    private Integer quantity;

    public static ItemResponse from(CartItem item) {
        return new ItemResponse(
            item.getProduct().getName(),
            item.getQuantity()
        );
    }

    public static ItemResponse from(OrderItem item) {
        return new ItemResponse(
            item.getProduct().getName(),
            item.getQuantity()
        );
    }
}
