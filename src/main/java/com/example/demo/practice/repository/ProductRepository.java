package com.example.demo.practice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.practice.dto.response.ProductResponse;
import com.example.demo.practice.entity.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT new com.example.demo.practice.dto.response.ProductResponse(
            p.id,
            p.name,
            p.category.id,
            p.description,
            p.price,
            p.stock
        )
        FROM Product p
        WHERE p.isActive = true
    """)
    Page<ProductResponse> findProducts(Pageable pageable);

    @Query("""
        SELECT new com.example.demo.practice.dto.response.ProductResponse(
            p.id,
            p.name,
            p.category.id,
            p.description,
            p.price,
            p.stock
        )
        FROM Product p 
        WHERE p.name
        LIKE %:keyword%
        AND p.isActive = true
    """)
    Page<ProductResponse> search(@Param("keyword") String keyword, Pageable pageable);
}
