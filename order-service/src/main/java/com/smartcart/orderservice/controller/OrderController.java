package com.smartcart.orderservice.controller;

import com.smartcart.orderservice.entity.Order;
import com.smartcart.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<Order> placeOrder(
            @PathVariable Long userId,
            @RequestParam String address) {
        return new ResponseEntity<>(orderService.placeOrder(userId, address), HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        return ResponseEntity.ok(orderService.getTotalRevenue());
    }

    @GetMapping("/summary")
    public ResponseEntity<java.util.Map<String, Object>> getSalesSummary() {
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("totalOrders", orderService.getTotalCount());
        summary.put("totalRevenue", orderService.getTotalRevenue());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<Order>> getOrdersByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(orderService.getOrdersByVendor(vendorId));
    }

    @GetMapping("/vendor/{vendorId}/payouts")
    public ResponseEntity<List<com.smartcart.orderservice.entity.VendorPayout>> getVendorPayouts(@PathVariable Long vendorId) {
        return ResponseEntity.ok(orderService.getVendorPayouts(vendorId));
    }
}
