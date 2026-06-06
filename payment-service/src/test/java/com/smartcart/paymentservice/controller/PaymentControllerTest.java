package com.smartcart.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcart.paymentservice.entity.Payment;
import com.smartcart.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder() throws Exception {
        Payment payment = Payment.builder().id(1L).paymentStatus("CREATED").build();
        when(paymentService.createOrder(2L, 3L, 100.0)).thenReturn(payment);

        mockMvc.perform(post("/api/payments/create-order")
                .param("userId", "2")
                .param("orderId", "3")
                .param("amount", "100.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.paymentStatus").value("CREATED"));
    }

    @Test
    void verifySignature() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("razorpay_order_id", "order_123");

        Payment payment = Payment.builder().id(1L).paymentStatus("SUCCESS").build();
        when(paymentService.verifySignature(any())).thenReturn(payment);

        mockMvc.perform(post("/api/payments/verify-signature")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
    }
}
