package com.example.demo.practice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.practice.dto.response.OrderResponse;
import com.example.demo.practice.entity.Cart;
import com.example.demo.practice.entity.Order;
import com.example.demo.practice.entity.OrderItem;
import com.example.demo.practice.entity.OrderStatus;
import com.example.demo.practice.entity.Product;
import com.example.demo.practice.entity.User;
import com.example.demo.practice.exception.EmptyCartException;
import com.example.demo.practice.exception.NotEnoughStockException;
import com.example.demo.practice.exception.NotFoundException;
import com.example.demo.practice.exception.OrderCanceledException;
import com.example.demo.practice.exception.OrderShippedException;
import com.example.demo.practice.repository.OrderRepository;
import com.example.demo.practice.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    
    @Mock
    UserRepository userRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderService orderService;

    @Test
    void updateOrderStatus_orderNotExist_shouldThrowException() {

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> orderService.updateOrderStatus(1L, OrderStatus.SHIPPED)
        );
    }

    @Test
    void updateOrderStatus_alreadyCanceled_shouldThrowException() {

        Order order = mockOrder(OrderStatus.CANCELED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(
            OrderCanceledException.class,
            () -> orderService.updateOrderStatus(1L, OrderStatus.SHIPPED)
        );
    }

    @Test
    void updateOrderStatus_toShipped_shouldUpdateStatus() {

        Order order = mockOrder(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        assertEquals(OrderStatus.SHIPPED, response.getStatus());
    }

    @Test
    void updateOrderStatus_toCanceled_shouldRestoreStock() {

        Product product = mockProduct();
        Order order = mockOrder(OrderStatus.PENDING_PAYMENT);
        order.addItem(new OrderItem(product, 10));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.updateOrderStatus(1L, OrderStatus.CANCELED);

        assertEquals(110, product.getStock());
        assertEquals(OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    void searchOrder_userMatches_shouldReturnOrderResponse() {

        Order order = mockOrder(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(order.getUser()));

        OrderResponse response = orderService.searchOrder("test@test.com", 1L);

        assertEquals("GH", response.getUserName());
        assertEquals(OrderStatus.PENDING_PAYMENT, response.getStatus());
    }

    @Test
    void searchOrder_orderNotExist_shouldThrowException() {

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> orderService.searchOrder("test@test.com", 1L)
        );
    }

    @Test
    void searchOrder_userNotMatch_shouldThrowException() {

        Order order = mockOrder(OrderStatus.PENDING_PAYMENT);
        User user2 = new User("2", "test2@test.com", "phone", "add", "pass");
        user2.setId(2L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(userRepository.findByEmail("test2@test.com")).thenReturn(Optional.of(user2));

        assertThrows(
            NotFoundException.class,
            () -> orderService.searchOrder("test2@test.com", 1L)
        );
    }

    @Test
    void cancelOrder_cancelSuccess_shouldBeCanceled() {

        Order order = mockOrder(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(order.getUser()));

        OrderResponse response = orderService.cancelOrder("test@test.com", 1L);

        assertEquals(OrderStatus.CANCELED, response.getStatus());
    }

    @Test
    void cancelOrder_alreadyShipped_shouldThrowException() {

        Order order = mockOrder(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(order.getUser()));

        assertThrows(
            OrderShippedException.class,
            () -> orderService.cancelOrder("test@test.com", 1L)
        );
    }

    @Test
    void cancelOrder_alreadyCanceled_shouldThrowException() {

        Order order = mockOrder(OrderStatus.CANCELED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(order.getUser()));

        assertThrows(
            OrderCanceledException.class,
            () -> orderService.cancelOrder("test@test.com", 1L)
        );
    }

    @Test
    void checkout_shouldReturnOrderResponse() {

        User user = mockUser();
        Cart cart = new Cart(user);
        Product product = mockProduct();
        cart.addItem(product, 10);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        OrderResponse response = orderService.checkout("test@test.com");

        assertEquals("GH", response.getUserName());
        assertEquals(0, response.getTotalPrice().compareTo(BigDecimal.valueOf(2000)));
        assertEquals(90, product.getStock());
        assertEquals(OrderStatus.PENDING_PAYMENT, response.getStatus());
        assertEquals(true, cart.getItems().isEmpty());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void checkout_emptyCart_shouldThrowException() {

        User user = mockUser();
        @SuppressWarnings("unused")
        Cart cart = new Cart(user);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        assertThrows(
            EmptyCartException.class,
            () -> orderService.checkout("test@test.com")
        );
    }

    @Test
    void checkout_productNotActive_shouldThrowException() {

        User user = mockUser();
        Cart cart = new Cart(user);
        Product product = mockProduct();
        product.delete();
        cart.addItem(product, 10);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        assertThrows(
            NotFoundException.class,
            () -> orderService.checkout("test@test.com")
        );
    }

    @Test
    void checkout_NotEnoughStock_shouldThrowException() {

        User user = mockUser();
        Cart cart = new Cart(user);
        Product product = mockProduct();
        cart.addItem(product, 110);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        assertThrows(
            NotEnoughStockException.class,
            () -> orderService.checkout("test@test.com")
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

    private Product mockProduct() {
        return new Product(
            "test product",
            "this is a test product",
            BigDecimal.valueOf(200),
            100
        );
    }
}
