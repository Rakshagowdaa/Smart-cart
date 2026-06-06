package com.smartcart.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @PutMapping("/api/products/{id}/deduct-stock")
    void deductStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @org.springframework.web.bind.annotation.GetMapping("/api/products/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);

    @lombok.Data
    class ProductDto {
        private Long id;
        private String name;
        private Double price;
        private Long vendorId;
    }
}
