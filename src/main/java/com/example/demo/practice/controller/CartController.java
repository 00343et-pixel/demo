package com.example.demo.practice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.practice.dto.request.CartItemRequest;
import com.example.demo.practice.dto.request.CartUpdateRequest;
import com.example.demo.practice.dto.response.CartResponse;
import com.example.demo.practice.dto.response.ErrorResponse;
import com.example.demo.practice.dto.response.OrderResponse;
import com.example.demo.practice.service.CartService;
import com.example.demo.practice.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Tag(name = "Cart", description = "購物車相關 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    
    private final CartService cartService;
    private final OrderService orderService;

    @Operation(
        summary = "查看購物車"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功顯示購物車"),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping
    public ResponseEntity<CartResponse> getCart(
        Authentication authentication
    ) {
        return ResponseEntity.ok(cartService.getCartResponse(authentication.getName()));
    }

    @Operation(
        summary = "新增購買商品",
        description = "輸入商品及數量"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "成功新增購買商品"),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
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
    @PostMapping("/items")
    public ResponseEntity<CartResponse> postItem(
            Authentication authentication,
            @RequestBody @Valid CartItemRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addCartItem(authentication.getName(), request));
    }

    @Operation(
        summary = "更新購買商品數量",
        description = "輸入商品數量"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功更新購買商品數量"),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
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
    @PutMapping("/items/{id}")
    public ResponseEntity<CartResponse> putItem(
            Authentication authentication,
            @RequestBody @Valid CartUpdateRequest request,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(cartService.updateCratItem(authentication.getName(), request, id));
    }

    @Operation(
        summary = "清除購買商品"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功清除購買商品"),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
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
    @DeleteMapping("/items/{id}")
    public ResponseEntity<CartResponse> deleteItem(
            Authentication authentication,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(cartService.deleteCartItem(authentication.getName(), id));
    }

    @Operation(
        summary = "結帳"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "成功建立訂單"),
            @ApiResponse(
                responseCode = "400",
                description = "購物車是空的",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
        Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkout(authentication.getName()));
    }
}
