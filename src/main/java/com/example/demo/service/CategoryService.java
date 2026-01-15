package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CategoryCreateRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;

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
