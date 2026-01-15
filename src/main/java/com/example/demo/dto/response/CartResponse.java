package com.example.demo.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "cart imformation")
public class CartResponse implements Serializable {

    @Schema(description = "user name", example = "GuoHong")
    private String userName;

    @Schema(description = "list of items")
    private List<ItemResponse> items;

    public static CartResponse from(Cart cart) {
        List<ItemResponse> items = new ArrayList<>();
        for (CartItem item : cart.getItems().values()) {
            items.add(ItemResponse.from(item));
        }
        return new CartResponse(
            cart.getUser().getName(),
            items
        );
    }
}
