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
import com.example.demo.practice.dto.response.ErrorResponse;
import com.example.demo.practice.dto.response.OrderResponse;
import com.example.demo.practice.dto.response.PageResponse;
import com.example.demo.practice.dto.response.ProductResponse;
import com.example.demo.practice.entity.OrderStatus;
import com.example.demo.practice.service.CategoryService;
import com.example.demo.practice.service.OrderService;
import com.example.demo.practice.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "成功新增商品類別"),
            @ApiResponse(
                responseCode = "400",
                description = "類別已存在",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "403",
                description = "權限不足",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(
        @RequestBody @Valid CategoryCreateRequest request
    ) {
        log.info("create category, name={}", request.categoryName());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @Operation(
        summary = "新增商品"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "成功新增商品"),
            @ApiResponse(
                responseCode = "400",
                description = "商品已存在",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "403",
                description = "權限不足",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "404",
                description = "商品或類別不存在",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(
        @RequestBody @Valid ProductCreateRequest request
    ) {
        log.info("create product, name={}", request.productName());
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }
    
    @Operation(
        summary = "修改商品"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功修改商品"),
            @ApiResponse(
                responseCode = "400",
                description = "商品已存在",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "403",
                description = "權限不足",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "404",
                description = "商品或類別不存在",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
        @RequestBody ProductUpdateRequest request,
        @PathVariable Long id
    ) {
        log.info("update product, id={}", id);
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @Operation(
        summary = "刪除商品"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "成功刪除商品"),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "403",
                description = "權限不足",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "404",
                description = "商品不存在",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long id
    ) {
        log.info("delete product, id={}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "用狀態查詢訂單列表"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功查詢訂單列表"),
            @ApiResponse(
                responseCode = "400",
                description = "status錯誤",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "403",
                description = "權限不足",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
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
        summary = "更新訂單狀態"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功更新訂單狀態"),
            @ApiResponse(
                responseCode = "400",
                description = "status錯誤",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "403",
                description = "權限不足",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "404",
                description = "訂單不存在",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
        @PathVariable Long id,
        @RequestBody @Valid OrderStatusRequest request
    ) {
        log.info("update order status, id={}", id);
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.orderStatus()));
    }
}
