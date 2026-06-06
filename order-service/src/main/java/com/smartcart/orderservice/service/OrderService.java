package com.smartcart.orderservice.service;

import com.smartcart.orderservice.client.CartClient;
import org.springframework.kafka.core.KafkaTemplate;
import com.smartcart.orderservice.dto.NotificationEvent;
import com.smartcart.orderservice.config.KafkaConfig;
import com.smartcart.orderservice.client.ProductClient;
import com.smartcart.orderservice.client.UserClient;
import com.smartcart.orderservice.entity.Order;
import com.smartcart.orderservice.entity.OrderItem;
import com.smartcart.orderservice.entity.VendorPayout;
import com.smartcart.orderservice.repository.OrderRepository;
import com.smartcart.orderservice.repository.VendorPayoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final VendorPayoutRepository vendorPayoutRepository;
    private final CartClient cartClient;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "productService", fallbackMethod = "placeOrderFallback")
    public Order placeOrder(Long userId, String address) {
        CartClient.CartDto cart = cartClient.getCartByUserId(userId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double totalAmount = cart.getTotalPrice();
        double finalAmount = totalAmount;

        List<OrderItem> orderItems = cart.getItems().stream().map(item -> {
            
            productClient.deductStock(item.getProductId(), item.getQuantity());
            
            // Fetch product to get vendorId
            ProductClient.ProductDto product = productClient.getProductById(item.getProductId());

            return OrderItem.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .vendorId(product.getVendorId())
                    .build();
        }).collect(Collectors.toList());

        Order order = Order.builder()
                .userId(userId)
                .items(orderItems)
                .totalAmount(totalAmount)
                .discountAmount(0.0)
                .finalAmount(finalAmount)
                .address(address)
                .status("PENDING")
                .createdAt(new Date())
                .build();

        Order savedOrder = orderRepository.save(order);
        
        // Calculate and save vendor payouts
        java.util.Map<Long, Double> vendorEarnings = orderItems.stream()
                .filter(item -> item.getVendorId() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        OrderItem::getVendorId,
                        java.util.stream.Collectors.summingDouble(item -> item.getPrice() * item.getQuantity())
                ));

        vendorEarnings.forEach((vendorId, amount) -> {
            VendorPayout payout = VendorPayout.builder()
                    .vendorId(vendorId)
                    .orderId(savedOrder.getId())
                    .amount(amount)
                    .status("PENDING")
                    .createdAt(new Date())
                    .build();
            vendorPayoutRepository.save(payout);
        });
        
        UserClient.UserDto user = userClient.getUserById(userId);
        if (user.isNewUser()) {
            userClient.updateNewUserFlag(userId, false);
        }
        
       
        try {
            NotificationEvent event = new NotificationEvent(
                userId,
                "Order Placed Successfully",
                "Your order #" + savedOrder.getId() + " has been placed. Waiting for payment.",
                "ORDER_PLACED"
            );
            kafkaTemplate.send(KafkaConfig.NOTIFICATION_TOPIC, event);
        } catch(Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return savedOrder;
    }

    public Order placeOrderFallback(Long userId, String address, Throwable throwable) {
        System.err.println("Fallback triggered for placeOrder: " + throwable.getMessage());
        throw new RuntimeException("Service is currently unavailable. Please try again later. (" + throwable.getMessage() + ")");
    }



    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrder(orderId);
        
        if ("DELIVERED".equals(order.getStatus())) {
            throw new RuntimeException("Cannot change status of a delivered order");
        }
        
        List<String> sequence = java.util.Arrays.asList("PENDING", "CREATED", "PROCESSING", "SHIPPED", "DELIVERED");
        int currentIndex = sequence.indexOf(order.getStatus());
        int newIndex = sequence.indexOf(status);
        
        if (currentIndex != -1 && newIndex != -1 && newIndex < currentIndex) {
            throw new RuntimeException("Cannot move order status backwards");
        }
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Double getTotalRevenue() {
        Double revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    public long getTotalCount() {
        return orderRepository.count();
    }

    public List<Order> getOrdersByVendor(Long vendorId) {
        return orderRepository.findByVendorId(vendorId);
    }

    public List<VendorPayout> getVendorPayouts(Long vendorId) {
        return vendorPayoutRepository.findByVendorId(vendorId);
    }
}
