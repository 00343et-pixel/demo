package com.example.demo.entity;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    @MapKey(name = "product")
    private Map<Product, CartItem> items = new HashMap<>();

    public Cart(User user) {
        this.user = user;
        user.setCart(this);
    }

    public void addItem(Product product, int quantity) {
        items.compute(product, (p, item) -> {
            if (item == null) {
                return new CartItem(this, p, quantity);
            }
            item.increaseQuantity(quantity);
            return item;
        });
        this.touch();
    }

    public void updateQuantity(Product product, int quantity) {
        items.compute(product, (p, item) -> {
            if (item == null) {
                return new CartItem(this, p, quantity);
            }
            item.setQuantity(quantity);
            return item;
        });
        this.touch();
    }

    public void deleteItem(Product product) {
        CartItem item = items.remove(product);
        if (item != null) {
            item.setCart(null);
        }
        this.touch();
    }

    public void clear() {
        for (CartItem item : items.values()) {
            item.setCart(null);
        }
        items.clear();
        this.touch();
    }
}
