package com.example.demo.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "order imformation")
public class OrderResponse implements Serializable {
    
    @Schema(description = "order ID", example = "1")
    private Long orderId;

    @Schema(description = "user name", example = "GuoHong")
    private String userName;

    @Schema(description = "list of items")
    private List<ItemResponse> items;

    @Schema(description = "total price", example = "23000.00")
    private BigDecimal totalPrice;

    @Schema(description = "order status", example = "PENDING_PAYMENT")
    private OrderStatus status;

    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getUser().getName(),
            order.getItems().stream()
                        .map(ItemResponse::from)
                        .toList(),
            order.getTotalPrice(),
            order.getStatus()
        );
    }
}
