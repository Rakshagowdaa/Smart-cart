package com.smartcart.orderservice.service;

import com.smartcart.orderservice.client.CartClient;
import com.smartcart.orderservice.client.ProductClient;
import com.smartcart.orderservice.client.UserClient;
import com.smartcart.orderservice.dto.NotificationEvent;
import com.smartcart.orderservice.config.KafkaConfig;
import com.smartcart.orderservice.entity.Order;
import com.smartcart.orderservice.entity.VendorPayout;
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

    // ─── placeOrder ────────────────────────────────────────────────────────────

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

        Order result = orderService.placeOrder(2L, "Address");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(productClient).deductStock(1L, 1);
        verify(kafkaTemplate).send(eq(KafkaConfig.NOTIFICATION_TOPIC), any(NotificationEvent.class));
    }

    @Test
    void placeOrder_NewUser_UpdatesFlag() {
        CartClient.CartDto cartDto = new CartClient.CartDto();
        cartDto.setTotalPrice(200.0);
        CartClient.CartItemDto item = new CartClient.CartItemDto();
        item.setProductId(5L);
        item.setProductName("Phone");
        item.setPrice(200.0);
        item.setQuantity(1);
        cartDto.setItems(Arrays.asList(item));
        when(cartClient.getCartByUserId(3L)).thenReturn(cartDto);

        UserClient.UserDto userDto = new UserClient.UserDto();
        userDto.setId(3L);
        userDto.setNewUser(true); // new user - flag should be updated
        when(userClient.getUserById(3L)).thenReturn(userDto);

        ProductClient.ProductDto productDto = new ProductClient.ProductDto();
        productDto.setId(5L);
        productDto.setVendorId(7L);
        when(productClient.getProductById(5L)).thenReturn(productDto);

        Order savedOrder = Order.builder().id(20L).finalAmount(200.0).build();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        orderService.placeOrder(3L, "New Address");

        verify(userClient).updateNewUserFlag(3L, false);
    }

    @Test
    void placeOrder_NoVendorId_SkipsPayoutCreation() {
        CartClient.CartDto cartDto = new CartClient.CartDto();
        cartDto.setTotalPrice(50.0);
        CartClient.CartItemDto item = new CartClient.CartItemDto();
        item.setProductId(9L);
        item.setProductName("Widget");
        item.setPrice(50.0);
        item.setQuantity(1);
        cartDto.setItems(Arrays.asList(item));
        when(cartClient.getCartByUserId(4L)).thenReturn(cartDto);

        ProductClient.ProductDto productDto = new ProductClient.ProductDto();
        productDto.setId(9L);
        productDto.setVendorId(null); // no vendorId
        when(productClient.getProductById(9L)).thenReturn(productDto);

        UserClient.UserDto userDto = new UserClient.UserDto();
        userDto.setId(4L);
        userDto.setNewUser(false);
        when(userClient.getUserById(4L)).thenReturn(userDto);

        Order savedOrder = Order.builder().id(30L).finalAmount(50.0).build();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.placeOrder(4L, "Some Street");

        assertNotNull(result);
        verify(vendorPayoutRepository, never()).save(any(VendorPayout.class));
    }

    @Test
    void placeOrder_KafkaFails_StillReturnsOrder() {
        CartClient.CartDto cartDto = new CartClient.CartDto();
        cartDto.setTotalPrice(100.0);
        CartClient.CartItemDto item = new CartClient.CartItemDto();
        item.setProductId(1L);
        item.setProductName("Item");
        item.setPrice(100.0);
        item.setQuantity(1);
        cartDto.setItems(Arrays.asList(item));
        when(cartClient.getCartByUserId(5L)).thenReturn(cartDto);

        ProductClient.ProductDto productDto = new ProductClient.ProductDto();
        productDto.setId(1L);
        productDto.setVendorId(2L);
        when(productClient.getProductById(1L)).thenReturn(productDto);

        UserClient.UserDto userDto = new UserClient.UserDto();
        userDto.setId(5L);
        userDto.setNewUser(false);
        when(userClient.getUserById(5L)).thenReturn(userDto);

        Order savedOrder = Order.builder().id(40L).build();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(kafkaTemplate.send(anyString(), any())).thenThrow(new RuntimeException("Kafka down"));

        Order result = orderService.placeOrder(5L, "Address");
        assertNotNull(result); // should not throw despite Kafka failure
    }

    @Test
    void placeOrder_CartEmpty() {
        CartClient.CartDto cartDto = new CartClient.CartDto();
        when(cartClient.getCartByUserId(2L)).thenReturn(cartDto);

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(2L, "Address"));
    }

    @Test
    void placeOrderFallback_ThrowsRuntimeException() {
        RuntimeException cause = new RuntimeException("circuit open");
        assertThrows(RuntimeException.class,
                () -> orderService.placeOrderFallback(1L, "addr", cause));
    }

    // ─── getOrder ──────────────────────────────────────────────────────────────

    @Test
    void getOrder_Success() {
        Order order = Order.builder().id(10L).build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder(10L);
        assertEquals(10L, result.getId());
    }

    @Test
    void getOrder_NotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.getOrder(99L));
    }

    // ─── getOrdersByUser ───────────────────────────────────────────────────────

    @Test
    void getOrdersByUser_ReturnsList() {
        List<Order> orders = Arrays.asList(Order.builder().id(1L).userId(2L).build());
        when(orderRepository.findByUserId(2L)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByUser(2L);
        assertEquals(1, result.size());
    }

    // ─── updateOrderStatus ─────────────────────────────────────────────────────

    @Test
    void updateOrderStatus_Success() {
        Order order = Order.builder().id(10L).status("PENDING").build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus(10L, "SHIPPED");
        assertEquals("SHIPPED", result.getStatus());
    }

    @Test
    void updateOrderStatus_DeliveredOrder_Throws() {
        Order order = Order.builder().id(10L).status("DELIVERED").build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(10L, "SHIPPED"));
    }

    @Test
    void updateOrderStatus_BackwardsTransition_Throws() {
        Order order = Order.builder().id(10L).status("SHIPPED").build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(10L, "PENDING"));
    }

    @Test
    void updateOrderStatus_UnknownStatus_AllowsUpdate() {
        Order order = Order.builder().id(10L).status("CUSTOM").build();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // currentIndex == -1, so backwards check does not apply
        Order result = orderService.updateOrderStatus(10L, "DELIVERED");
        assertEquals("DELIVERED", result.getStatus());
    }

    // ─── getAllOrders ──────────────────────────────────────────────────────────

    @Test
    void getAllOrders_ReturnsList() {
        List<Order> orders = Arrays.asList(Order.builder().id(1L).build(), Order.builder().id(2L).build());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();
        assertEquals(2, result.size());
    }

    // ─── getTotalRevenue ───────────────────────────────────────────────────────

    @Test
    void getTotalRevenue_ReturnsValue() {
        when(orderRepository.getTotalRevenue()).thenReturn(500.0);
        assertEquals(500.0, orderService.getTotalRevenue());
    }

    @Test
    void getTotalRevenue_NullFromRepo_ReturnsZero() {
        when(orderRepository.getTotalRevenue()).thenReturn(null);
        assertEquals(0.0, orderService.getTotalRevenue());
    }

    // ─── getTotalCount ─────────────────────────────────────────────────────────

    @Test
    void getTotalCount_ReturnsCount() {
        when(orderRepository.count()).thenReturn(5L);
        assertEquals(5L, orderService.getTotalCount());
    }

    // ─── getOrdersByVendor ─────────────────────────────────────────────────────

    @Test
    void getOrdersByVendor_ReturnsList() {
        List<Order> orders = Arrays.asList(Order.builder().id(1L).build());
        when(orderRepository.findByVendorId(10L)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByVendor(10L);
        assertEquals(1, result.size());
    }

    // ─── getVendorPayouts ──────────────────────────────────────────────────────

    @Test
    void getVendorPayouts_ReturnsList() {
        List<VendorPayout> payouts = Arrays.asList(
                VendorPayout.builder().id(1L).vendorId(10L).amount(200.0).build());
        when(vendorPayoutRepository.findByVendorId(10L)).thenReturn(payouts);

        List<VendorPayout> result = orderService.getVendorPayouts(10L);
        assertEquals(1, result.size());
        assertEquals(200.0, result.get(0).getAmount());
    }
}
