package com.example.demo.practice.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.practice.dto.request.CategoryCreateRequest;
import com.example.demo.practice.dto.request.OrderStatusRequest;
import com.example.demo.practice.dto.request.ProductCreateRequest;
import com.example.demo.practice.dto.request.ProductUpdateRequest;
import com.example.demo.practice.dto.response.CategoryResponse;
import com.example.demo.practice.dto.response.OrderResponse;
import com.example.demo.practice.dto.response.PageResponse;
import com.example.demo.practice.dto.response.ProductResponse;
import com.example.demo.practice.entity.OrderStatus;
import com.example.demo.practice.service.CategoryService;
import com.example.demo.practice.service.OrderService;
import com.example.demo.practice.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Admin", description = "管理員相關 API")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    
    @Operation(
        summary = "新增商品類別"
    )
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(
        @RequestBody @Valid CategoryCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @Operation(
        summary = "新增商品"
    )
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(
        @RequestBody @Valid ProductCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }
    
    @Operation(
        summary = "修改商品"
    )
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
        @RequestBody ProductUpdateRequest request,
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @Operation(
        summary = "刪除商品"
    )
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "所有訂單（?status=篩選)"
    )
    @GetMapping("/orders")
    public PageResponse<OrderResponse> getOrders(
            @RequestParam OrderStatus status,
            @PageableDefault(
                page = 0,
                size = 10,
                sort = "id",
                direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return orderService.findOrderStatus(status, pageable);
    }

    @Operation(
        summary = "更新出貨狀態"
    )
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
        @PathVariable Long id,
        @RequestBody @Valid OrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }
}
