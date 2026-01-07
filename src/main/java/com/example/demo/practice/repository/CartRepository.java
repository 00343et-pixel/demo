package com.example.demo.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.practice.entity.Cart;


public interface CartRepository extends JpaRepository<Cart, Long> {
    
}
