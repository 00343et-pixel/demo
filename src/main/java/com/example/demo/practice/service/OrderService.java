package com.example.demo.practice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.practice.dto.request.OrderStatusRequest;
import com.example.demo.practice.dto.response.OrderResponse;
import com.example.demo.practice.dto.response.PageResponse;
import com.example.demo.practice.entity.Cart;
import com.example.demo.practice.entity.CartItem;
import com.example.demo.practice.entity.Order;
import com.example.demo.practice.entity.OrderItem;
import com.example.demo.practice.entity.OrderStatus;
import com.example.demo.practice.entity.Product;
import com.example.demo.practice.entity.User;
import com.example.demo.practice.exception.NotEnoughStockException;
import com.example.demo.practice.exception.NotFoundException;
import com.example.demo.practice.repository.OrderRepository;
import com.example.demo.practice.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Cacheable(
        value = "orderPage",
        key = "'email:' + #email"
            + " + ':p:' + #pageable.pageNumber"
            + " + ':s:' + #pageable.pageSize"
            + " + ':sort:' + #pageable.sort",
        condition = "#pageable.pageNumber == 0"
    )
    public PageResponse<OrderResponse> findUserOrders(String email, Pageable pageable) {
        Page<Long> page = orderRepository.findOrderIdsByUserId(getUser(email).getId(), pageable);
        return changeToResponse(page);
    }

    @Transactional(readOnly = true)
    @Cacheable(
        value = "orderPage",
        key = "'status:' + #status"
            + " + ':p:' + #pageable.pageNumber"
            + " + ':s:' + #pageable.pageSize"
            + " + ':sort:' + #pageable.sort",
        condition = "#pageable.pageNumber == 0"
    )
    public PageResponse<OrderResponse> findOrderStatus(OrderStatus status, Pageable pageable) {
        System.out.println("status = " + status);
        Page<Long> page = orderRepository.findOrderIdsByStatus(status, pageable);
        return changeToResponse(page);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "orderPage", allEntries = true),
        @CacheEvict(value = "order", allEntries = true)
    })
    public OrderResponse updateOrderStatus(Long id, OrderStatusRequest request) {

        Order order = getOrder(id);
        OrderStatus status = request.orderStatus();

        order.setStatus(status);

        if (status == OrderStatus.CANCELED) {
            restoreStock(order);
        }
        
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "order", key = "'email:' + #email + ':id:' + #id")
    public OrderResponse searchOrder(String email, Long id) {

        return OrderResponse.from(getOrder(email, id));
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "orderPage", allEntries = true),
        @CacheEvict(value = "order", key = "'email:' + #email + ':id:' + #id")
    })
    public OrderResponse cancelOrder(String email, Long id) {

        Order order = getOrder(email, id);
        OrderStatus status = order.getStatus();

        if (status == OrderStatus.SHIPPED) {
            throw new RuntimeException("order is shipped, cannot be canceled");
        }

        order.setStatus(OrderStatus.CANCELED);
        restoreStock(order);
        
        return OrderResponse.from(order);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "orders", key = "'email:' + #email"),
        @CacheEvict(value = "cart", key = "'email:' + #email"),
        @CacheEvict(value = "orderPage", allEntries = true)
    })
    public OrderResponse checkout(String email) {

        User user = getUser(email);
        Cart cart = user.getCart();

        if (cart.getItems().isEmpty()) { throw new RuntimeErrorException(null, "cart is empty"); }

        Order order = new Order();

        for (CartItem item : cart.getItems().values()) {
            Product product = item.getProduct();
            Integer quantity = item.getQuantity();
            
            if (product.getIsActive() == false) {
                throw new RuntimeErrorException(null, "product is not available now");
            }
            if (quantity > product.getStock()) {
                throw new NotEnoughStockException();
            }

            BigDecimal price = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            OrderItem orderItem = new OrderItem(product, quantity, price);
            order.addItem(orderItem);
            product.decreaseStock(quantity);
        }

        user.addOrder(order);
        orderRepository.save(order);
        cart.clear();

        return OrderResponse.from(order);
    }

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user not exists"));
    }

    private Order getOrder(String email, Long id) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("order not exists"));
        if (order.getUser() != getUser(email)){
            throw new NotFoundException("order not exists");
        }
        return order;
    }

    private Order getOrder(Long id) {

        return orderRepository.findById(id).orElseThrow(() -> new NotFoundException("order not exists"));
    }

    private void restoreStock(Order order) {

        for (OrderItem item : order.getItems()) {
            item.getProduct().increaseStock(item.getQuantity());
        }
    }

    private PageResponse<OrderResponse> changeToResponse(Page<Long> page) {

        List<Long> orderIds = page.getContent();

        if (orderIds.isEmpty()) {
            return new PageResponse<>(
                List.of(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
            );
        }

        List<Order> ordersWithItems =
                orderRepository.findWithItemsByIds(orderIds);

        Map<Long, Order> orderMap = ordersWithItems.stream()
                .collect(Collectors.toMap(Order::getId, o -> o));

        return new PageResponse<OrderResponse>(
            orderIds.stream()
                    .map(id -> OrderResponse.from(orderMap.get(id)))
                    .toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }
}
