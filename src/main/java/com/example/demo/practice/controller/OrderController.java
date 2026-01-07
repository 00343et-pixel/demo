package com.example.demo.practice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.practice.dto.response.ErrorResponse;
import com.example.demo.practice.dto.response.OrderResponse;
import com.example.demo.practice.dto.response.PageResponse;
import com.example.demo.practice.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Tag(name = "Order", description = "訂單相關 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(
        summary = "使用者訂單列表"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功顯示使用者訂單列表"),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
                    content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping
    public PageResponse<OrderResponse> getOrders(
            Authentication authentication,
            @PageableDefault(page = 0,
                size = 10,
                sort = "id",
                direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return orderService.findUserOrders(authentication.getName(), pageable);
    }

    @Operation(
        summary = "訂單詳細"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功顯示訂單詳細"),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
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
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            Authentication authentication,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.searchOrder(authentication.getName(), id));
    }

    @Operation(
        summary = "取消訂單"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功取消訂單"),
            @ApiResponse(
                responseCode = "401",
                description = "未登入",
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
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrderById(
            Authentication authentication,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(authentication.getName(), id));
    }

}
