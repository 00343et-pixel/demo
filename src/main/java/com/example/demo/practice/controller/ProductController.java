package com.example.demo.practice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.practice.dto.response.ErrorResponse;
import com.example.demo.practice.dto.response.PageResponse;
import com.example.demo.practice.dto.response.ProductResponse;
import com.example.demo.practice.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Tag(name = "Product", description = "商品相關 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService service;
    
    @Operation(
        summary = "商品列表"
    )
    @ApiResponse(responseCode = "200",
            description = "成功顯示商品列表"
    )
    @GetMapping
    public PageResponse<ProductResponse> findAll(
            @PageableDefault(
                page = 0,
                size = 10,
                sort = "id",
                direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {        
        return service.findProducts(pageable);
    }

    @Operation(
        summary = "關鍵字查詢商品列表"
    )
    @ApiResponse(responseCode = "200",
            description = "成功查詢商品列表"
    )
    @GetMapping("/search")
    public PageResponse<ProductResponse> searchByName(
            @RequestParam String keyword,
            @PageableDefault(
                page = 0,
                size = 10,
                sort = "id",
                direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return service.search(keyword, pageable);
    }

    @Operation(
        summary = "商品詳細資料"
    )
    @ApiResponses({
                @ApiResponse(responseCode = "200",
                        description = "成功顯示商品詳細資料"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "商品不存在",
                        content = @Content(
                                schema = @Schema(implementation = ErrorResponse.class)
                ))
        })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getId(
        @Parameter(description = "product ID", example = "1")
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }
    
}
