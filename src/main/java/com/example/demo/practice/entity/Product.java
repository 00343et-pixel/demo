package com.example.demo.practice.entity;

import com.example.demo.practice.exception.InvalidQuantityException;
import com.example.demo.practice.exception.NotEnoughStockException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Product(
        String name,
        String description,
        Integer price,
        Integer stock
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.isActive = false;
    }

    // 設定類別
    public void setCategory(Category category) {
            this.category = category;
    }

    // 更新
    public void updateProduct(
        String name,
        String description,
        Integer price,
        Integer stock,
        Boolean isActive
    ) {
        if (name != null) { this.name = name; }
        if (description != null) { this.description = description; }
        if (price != null) { this.price = price; }
        if (stock != null) { this.stock = stock; }
        if (isActive != null) { this.isActive = isActive; }
    }

    //增減庫存
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        this.stock += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        if (quantity > this.stock) {
            throw new NotEnoughStockException();
        }
        this.stock -= quantity;
    }

    // 只用 id，比對 DB identity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
