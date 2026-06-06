package com.smartcart.paymentservice.service;

import com.smartcart.paymentservice.entity.Payment;
import com.smartcart.paymentservice.repository.PaymentRepository;
import com.smartcart.paymentservice.dto.NotificationEvent;
import com.smartcart.paymentservice.config.KafkaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "keyId", "rzp_test_mocked");
        ReflectionTestUtils.setField(paymentService, "keySecret", "mocked_secret_key");
    }

    @Test
    void verifySignature_PaymentNotFound() {
        Map<String, String> response = new HashMap<>();
        response.put("razorpay_order_id", "order_123");

        when(paymentRepository.findByRazorpayOrderId("order_123")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paymentService.verifySignature(response));
    }

    @Test
    void verifySignature_InvalidSignature() {
        Map<String, String> response = new HashMap<>();
        response.put("razorpay_order_id", "order_123");
        response.put("razorpay_payment_id", "pay_456");
        response.put("razorpay_signature", "invalid_sig");

        Payment payment = Payment.builder().id(1L).build();
        when(paymentRepository.findByRazorpayOrderId("order_123")).thenReturn(Optional.of(payment));

        try (org.mockito.MockedStatic<com.razorpay.Utils> mockedUtils = mockStatic(com.razorpay.Utils.class)) {
            mockedUtils.when(() -> com.razorpay.Utils.verifyPaymentSignature(any(), anyString())).thenReturn(false);

            assertThrows(RuntimeException.class, () -> paymentService.verifySignature(response));
            assertEquals("FAILED", payment.getPaymentStatus());
        }
    }

    @Test
    void verifySignature_Success() throws Exception {
        Map<String, String> response = new HashMap<>();
        response.put("razorpay_order_id", "order_123");
        response.put("razorpay_payment_id", "pay_456");
        response.put("razorpay_signature", "valid_sig");

        Payment payment = Payment.builder().id(1L).userId(2L).amount(100.0).orderId(3L).build();
        when(paymentRepository.findByRazorpayOrderId("order_123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        try (org.mockito.MockedStatic<com.razorpay.Utils> mockedUtils = mockStatic(com.razorpay.Utils.class)) {
            mockedUtils.when(() -> com.razorpay.Utils.verifyPaymentSignature(any(), anyString())).thenReturn(true);

            Payment result = paymentService.verifySignature(response);

            assertEquals("SUCCESS", result.getPaymentStatus());
            verify(paymentRepository).save(payment);
            verify(kafkaTemplate, times(1)).send(eq(KafkaConfig.NOTIFICATION_TOPIC), any(NotificationEvent.class));
        }
    }

    @Test
    void createOrder_Failure() {
        // Will throw RazorpayException due to invalid mocked auth inside the try block during `.orders.create()`
        assertThrows(RuntimeException.class, () -> paymentService.createOrder(1L, 2L, 100.0));
    }
}

