package com.example.demo.practice.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @DecimalMin(value = "0.00", inclusive = true)
    @Column(
        precision = 10,
        scale = 2,
        nullable = false
    )
    private BigDecimal price;

    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
