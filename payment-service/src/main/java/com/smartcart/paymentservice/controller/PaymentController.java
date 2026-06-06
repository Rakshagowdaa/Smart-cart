package com.smartcart.paymentservice.controller;

import com.smartcart.paymentservice.entity.Payment;
import com.smartcart.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<Payment> createOrder(
            @RequestParam Long userId,
            @RequestParam Long orderId,
            @RequestParam Double amount) {
        return ResponseEntity.ok(paymentService.createOrder(userId, orderId, amount));
    }

    @PostMapping("/verify-signature")
    public ResponseEntity<Payment> verifySignature(@RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(paymentService.verifySignature(payload));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok("Webhook processed successfully");
    }
}
