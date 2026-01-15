package com.example.demo.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.request.CartUpdateRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.NotEnoughStockException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CartService {
    
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Cacheable(value = "cart", key = "'email:' + #email")
    public CartResponse getCartResponse(String email) {

        return CartResponse.from(getCart(email));
    }

    @Transactional
    @CacheEvict(value = "cart", key = "'email:' + #email")
    public CartResponse addCartItem(String email, CartItemRequest request) {

        Cart cart = getCart(email);
        Product product = getProduct(request.productId());

        checkQuantity(request.quantity(), product);

        cart.addItem(product, request.quantity());

        checkQuantity(cart.getItems().get(product).getQuantity(), product);
        return CartResponse.from(cart);
    }

    @Transactional
    @CacheEvict(value = "cart", key = "'email:' + #email")
    public CartResponse updateCratItem(String email, CartUpdateRequest request, Long id) {

        Cart cart = getCart(email);
        Product product = getProduct(id);
        
        checkQuantity(request.quantity(), product);

        cart.updateQuantity(product, request.quantity());
        return CartResponse.from(cart);
    }

    @Transactional
    @CacheEvict(value = "cart", key = "'email:' + #email")
    public CartResponse deleteCartItem(String email, Long id) {

        Cart cart = getCart(email);
        Product product = getProduct(id);

        cart.deleteItem(product);
        
        return CartResponse.from(cart);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not exists."));
    }

    private Cart getCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not exists."));
        if (user.getCart() == null) {
            Cart cart = new Cart(user);
            cartRepository.save(cart);
        }
        return user.getCart();
    }

    private void checkQuantity(Integer quantity, Product product) {
        if (product.getIsActive() == false) {
            throw new NotFoundException("Product not exists.");
        }
        if (quantity > product.getStock()) {
            throw new NotEnoughStockException();
        }
    }

}
