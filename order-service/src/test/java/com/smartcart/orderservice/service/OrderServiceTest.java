package com.smartcart.orderservice.service;

import com.smartcart.orderservice.client.CartClient;
import com.smartcart.orderservice.client.ProductClient;
import com.smartcart.orderservice.client.UserClient;
import com.smartcart.orderservice.dto.NotificationEvent;
import com.smartcart.orderservice.config.KafkaConfig;
import com.smartcart.orderservice.entity.Order;
import com.smartcart.orderservice.repository.CouponRepository;
import com.smartcart.orderservice.repository.OrderRepository;
import com.smartcart.orderservice.repository.VendorPayoutRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private VendorPayoutRepository vendorPayoutRepository;

    @Mock
    private CartClient cartClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private UserClient userClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrder_Success_NoCoupon() {
        CartClient.CartDto cartDto = new CartClient.CartDto();
        cartDto.setTotalPrice(100.0);
        CartClient.CartItemDto item = new CartClient.CartItemDto();
        item.setProductId(1L);
        item.setProductName("Laptop");
        item.setPrice(100.0);
        item.setQuantity(1);
        cartDto.setItems(Arrays.asList(item));

        when(cartClient.getCartByUserId(2L)).thenReturn(cartDto);

        UserClient.UserDto userDto = new UserClient.UserDto();
        userDto.setId(2L);
        userDto.setNewUser(false);
        when(userClient.getUserById(2L)).thenReturn(userDto);

        ProductClient.ProductDto productDto = new ProductClient.ProductDto();
        productDto.setId(1L);
        productDto.setVendorId(3L);
        when(productClient.getProductById(1L)).thenReturn(productDto);

        Order savedOrder = Order.builder().id(10L).finalAmount(100.0).build();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.placeOrder(2L, null, "Address");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(productClient).deductStock(1L, 1);
        verify(kafkaTemplate).send(eq(KafkaConfig.NOTIFICATION_TOPIC), any(NotificationEvent.class));
    }

    @Test
    void placeOrder_CartEmpty() {
        CartClient.CartDto cartDto = new CartClient.CartDto(); 
        when(cartClient.getCartByUserId(2L)).thenReturn(cartDto);

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(2L, null, "Address"));
    }

    @Test
    void getOrder_Success() {
        Order order = Order.builder().id(10L).build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder(10L);
        assertEquals(10L, result.getId());
    }

    @Test
    void updateOrderStatus_Success() {
        Order order = Order.builder().id(10L).status("PENDING").build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus(10L, "SHIPPED");
        assertEquals("SHIPPED", result.getStatus());
    }
}

