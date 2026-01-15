package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;


public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT o.id
        FROM Order o
        WHERE o.user.id = :userId
    """)
    Page<Long> findOrderIdsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT o.id
        FROM Order o
        WHERE o.status = :status
    """)
    Page<Long> findOrderIdsByStatus(@Param("status") OrderStatus status, Pageable pageable);

    @Query("""
        SELECT DISTINCT o
        FROM Order o
        JOIN FETCH o.items
        JOIN FETCH o.user
        WHERE o.id IN :orderIds
    """)
    List<Order> findWithItemsByIds(@Param("orderIds") List<Long> orderIds);
}