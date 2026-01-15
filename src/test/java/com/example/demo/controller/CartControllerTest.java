package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.request.CartUpdateRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;


@WebMvcTest(CartController.class)
public class CartControllerTest {
    
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CartService cartService;

    @MockBean
    OrderService orderService;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(
                invocation.getArgument(0),
                invocation.getArgument(1)
            );
            return null;
        }).when(jwtAuthenticationFilter)
        .doFilter(any(), any(), any());
    }

    @Test
    void getCart_unauthenticated_shouldReturn401() throws Exception {
        
        mockMvc.perform(get("/cart"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void getCart_authenticated_shouldReturnCartResponse() throws Exception {

        Cart cart = mockCart();

        when(cartService.getCartResponse("test@test.com"))
            .thenReturn(CartResponse.from(cart));

        mockMvc.perform(get("/cart"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userName").value("GH"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void postItem_authenticated_shouldAddItem() throws Exception {

        Cart cart = mockCart();
        CartItemRequest request = new CartItemRequest(1L, 10);
        Product product = mockProduct();
        cart.addItem(product, 10);

        when(cartService.addCartItem("test@test.com", request))
            .thenReturn(CartResponse.from(cart));

        mockMvc.perform(
                post("/cart/items")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userName").value("GH"))
            .andExpect(jsonPath("$.items[0].productName").value("test product"))
            .andExpect(jsonPath("$.items[0].quantity").value(10));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void postItem_invalidRequest_shouldReturn400() throws Exception {

        CartItemRequest request = new CartItemRequest(1L, 0);

        mockMvc.perform(
                post("/cart/items")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void putItem_authenticated_shouldUpdateItem() throws Exception {

        Cart cart = mockCart();
        CartUpdateRequest request = new CartUpdateRequest(10);
        Product product = mockProduct();
        cart.updateQuantity(product, 10);

        when(cartService.updateCratItem("test@test.com", request, 1L))
            .thenReturn(CartResponse.from(cart));

        mockMvc.perform(
                put("/cart/items/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userName").value("GH"))
            .andExpect(jsonPath("$.items[0].productName").value("test product"))
            .andExpect(jsonPath("$.items[0].quantity").value(10));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void putItem_invalidRequest_shouldReturn400() throws Exception {

        CartUpdateRequest request = new CartUpdateRequest(0);

        mockMvc.perform(
                put("/cart/items/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void deleteItem_authenticated_shouldDeleteItem() throws Exception {

        Cart cart = mockCart();

        when(cartService.deleteCartItem("test@test.com", 1L))
            .thenReturn(CartResponse.from(cart));

        mockMvc.perform(delete("/cart/items/1").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userName").value("GH"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void checkout_authenticated_shouldReturnOrderResponse() throws Exception {
        
        Product product = mockProduct();
        Order order = mockOrder(OrderStatus.PENDING_PAYMENT);
        order.addItem(new OrderItem(product, 10));

        when(orderService.checkout("test@test.com"))
            .thenReturn(OrderResponse.from(order));
        
        mockMvc.perform(post("/cart/checkout").with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userName").value("GH"))
            .andExpect(jsonPath("$.items[0].productName").value("test product"))
            .andExpect(jsonPath("$.items[0].quantity").value(10))
            .andExpect(jsonPath("$.totalPrice").value("2000.0"))
            .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }

    private Cart mockCart() {
        return new Cart(mockUser());
    }

    private Product mockProduct() {
        return new Product(
            "test product",
            "this is a test product",
            BigDecimal.valueOf(200),
            100
        );
    }

    private User mockUser() {
        User user = new User(
            "GH",
            "test@test.com",
            "0912345678",
            "Taipei",
            "password"
        );
        user.setId(1L);
        return user;
    }

    private Order mockOrder(OrderStatus status) {
        Order order = new Order();
        User user = mockUser();
        user.addOrder(order);
        order.setStatus(status);
        return order;
    }
}
