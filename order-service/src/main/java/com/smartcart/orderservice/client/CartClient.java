package com.smartcart.orderservice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "CART-SERVICE")
public interface CartClient {

    @GetMapping("/api/cart/{userId}")
    CartDto getCartByUserId(@PathVariable("userId") Long userId);

    @Data
    class CartDto {
        private Long id;
        private Long userId;
        private List<CartItemDto> items;
        private Double totalPrice;
    }

    @Data
    class CartItemDto {
        private Long productId;
        private String productName;
        private Double price;
        private Integer quantity;
    }
}
