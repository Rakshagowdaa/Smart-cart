package com.smartcart.orderservice.controller;


import com.smartcart.orderservice.entity.Order;
import com.smartcart.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;


    @Test
    void placeOrder() throws Exception {
        Order order = Order.builder().id(1L).userId(2L).finalAmount(100.0).build();
        when(orderService.placeOrder(2L, "CODE", "Address")).thenReturn(order);

        mockMvc.perform(post("/api/orders/2")
                .param("couponCode", "CODE")
                .param("address", "Address"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getOrder() throws Exception {
        Order order = Order.builder().id(1L).finalAmount(100.0).build();
        when(orderService.getOrder(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getOrdersByUser() throws Exception {
        Order order = Order.builder().id(1L).userId(2L).build();
        when(orderService.getOrdersByUser(2L)).thenReturn(Arrays.asList(order));

        mockMvc.perform(get("/api/orders/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void updateOrderStatus() throws Exception {
        Order order = Order.builder().id(1L).status("SHIPPED").build();
        when(orderService.updateOrderStatus(1L, "SHIPPED")).thenReturn(order);

        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void getAllOrders() throws Exception {
        Order order = Order.builder().id(1L).build();
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(order));

        mockMvc.perform(get("/api/orders/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getTotalRevenue() throws Exception {
        when(orderService.getTotalRevenue()).thenReturn(1500.0);

        mockMvc.perform(get("/api/orders/revenue"))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.0"));
    }

    @Test
    void getSalesSummary() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("totalOrders", 1L);
        map.put("totalRevenue", 1500.0);
        when(orderService.getTotalCount()).thenReturn(1L);
        when(orderService.getTotalRevenue()).thenReturn(1500.0);

        mockMvc.perform(get("/api/orders/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(1))
                .andExpect(jsonPath("$.totalRevenue").value(1500.0));
    }
}
