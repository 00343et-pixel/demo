package com.example.demo.practice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.practice.dto.request.CategoryCreateRequest;
import com.example.demo.practice.dto.response.CategoryResponse;
import com.example.demo.practice.entity.Category;
import com.example.demo.practice.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        Category category = new Category(request.categoryName());
        categoryRepository.save(category);
        return CategoryResponse.from(category);
    }
}
