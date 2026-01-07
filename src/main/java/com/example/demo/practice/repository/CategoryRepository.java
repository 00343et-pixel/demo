package com.example.demo.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.practice.entity.Category;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}
