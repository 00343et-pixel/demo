package com.example.demo.practice.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.practice.dto.request.ProductCreateRequest;
import com.example.demo.practice.dto.request.ProductUpdateRequest;
import com.example.demo.practice.dto.response.PageResponse;
import com.example.demo.practice.dto.response.ProductResponse;
import com.example.demo.practice.entity.Category;
import com.example.demo.practice.entity.Product;
import com.example.demo.practice.exception.InvalidQuantityException;
import com.example.demo.practice.exception.NotFoundException;
import com.example.demo.practice.repository.CategoryRepository;
import com.example.demo.practice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Category category = getCategory(request.categoryId());
        checkQuantity(request.price());
        checkQuantity(request.stock());
        Product product = new Product(
            request.productName(),
            request.description(),
            request.price(),
            request.stock()
        );
        category.addProduct(product);
        productRepository.save(product);
        return ProductResponse.from(product);
    }

    @Cacheable(value = "products", key = "#id")
    
    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProduct(id));
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "products", key = "#id"),
        @CacheEvict(value = "productPage", allEntries = true),
        @CacheEvict(value = "productSearchPage", allEntries = true)
    })
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = getProduct(id);
        checkQuantity(request.price());
        checkQuantity(request.stock());
        product.updateProduct(
            request.name(),
            request.description(),
            request.price(),
            request.stock(),
            request.isActive()
        );
        if (request.categoryId() != null) {
            product.getCategory().removeProduct(product);
            Category category = getCategory(request.categoryId());
            category.addProduct(product);
        }
        return ProductResponse.from(product);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "products", key = "#id"),
        @CacheEvict(value = "productPage", allEntries = true),
        @CacheEvict(value = "productSearchPage", allEntries = true)
    })
    public void deleteProduct(Long id) {
        /* 
            有 orphanRemoval → 一律用 parent 管 child
            不能 productRepository.deleteById(id);
         */
        Product product = getProduct(id);

        Category category = product.getCategory();

        category.removeProduct(product);
    }

    @Cacheable(
        value = "productPage",
        key = "'p:' + #pageable.pageNumber"
            + " + ':s:' + #pageable.pageSize"
            + " + ':sort:' + #pageable.sort",
        condition = "#pageable.pageNumber == 0"
    )
    public PageResponse<ProductResponse> findProducts(Pageable pageable) {

        Page<ProductResponse> page = productRepository.findProducts(pageable);
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Cacheable(
        value = "productSearchPage",
        key = "'kw:' + #keyword"
            + " + ':p:' + #pageable.pageNumber"
            + " + ':s:' + #pageable.pageSize"
            + " + ':sort:' + #pageable.sort",
        condition = "#pageable.pageNumber == 0"
    )
    public PageResponse<ProductResponse> search(String keyword, Pageable pageable) {

        Page<ProductResponse> page = productRepository.search(keyword, pageable);
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("product not exists"));
    }
    
    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("category not exists"));
    }

    private void checkQuantity(Integer quantity) {
        if (quantity < 0) { throw new InvalidQuantityException(); }
    }
}
