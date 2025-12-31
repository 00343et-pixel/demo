package com.example.demo.practice.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.practice.entity.Order;
import com.example.demo.practice.entity.OrderItem;
import com.example.demo.practice.entity.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "order imformation")
public class OrderResponse implements Serializable {

    @Schema(description = "user name", example = "GuoHong")
    private String userName;

    @Schema(description = "order ID", example = "1")
    private Long orderId;

    @Schema(description = "list of items")
    private List<ItemResponse> items;

    @Schema(description = "total price", example = "23000")
    private Integer totalPrice;

    @Schema(description = "order status", example = "PENDING_PAYMENT")
    private OrderStatus status;

    public static OrderResponse from(Order order) {
        List<ItemResponse> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            items.add(ItemResponse.from(item));
        }
        return new OrderResponse(
            order.getUser().getName(),
            order.getId(),
            items,
            order.getTotalPrice(),
            order.getStatus()
        );
    }
}
