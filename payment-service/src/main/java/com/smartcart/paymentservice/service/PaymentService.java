package com.smartcart.paymentservice.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.smartcart.paymentservice.entity.Payment;
import com.smartcart.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import com.smartcart.paymentservice.config.KafkaConfig;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    public Payment createOrder(Long userId, Long orderId, Double amount) {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", Math.round(amount * 100)); 
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + orderId);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            Payment payment = Payment.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .amount(amount)
                    .currency("INR")
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .paymentStatus("CREATED")
                    .createdAt(new Date())
                    .build();

            return paymentRepository.save(payment);
        } catch (RazorpayException e) {
            throw new RuntimeException("Error creating Razorpay order", e);
        }
    }

    public Payment verifySignature(Map<String, String> response) {
        String razorpayOrderId = response.get("razorpay_order_id");
        String razorpayPaymentId = response.get("razorpay_payment_id");
        String razorpaySignature = response.get("razorpay_signature");

        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

           
            boolean isValid = Utils.verifyPaymentSignature(attributes, keySecret);

            if (isValid) {
                payment.setRazorpayPaymentId(razorpayPaymentId);
                payment.setRazorpaySignature(razorpaySignature);
                payment.setPaymentStatus("SUCCESS");
            } else {
                payment.setPaymentStatus("FAILED");
                throw new RuntimeException("Invalid Signature");
            }

            Payment savedPayment = paymentRepository.save(payment);
            
            if (isValid) {
                try {
                    com.smartcart.paymentservice.dto.NotificationEvent event = new com.smartcart.paymentservice.dto.NotificationEvent(
                        savedPayment.getUserId(),
                        "Payment Successful",
                        "Your payment of " + savedPayment.getAmount() + " INR for order #" + savedPayment.getOrderId() + " was successful.",
                        "PAYMENT_SUCCESS"
                    );
                    kafkaTemplate.send(KafkaConfig.NOTIFICATION_TOPIC, event);
                } catch (Exception e) {
                    System.err.println("Failed to send notification: " + e.getMessage());
                }
            }

            return savedPayment;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void handleWebhook(String payload, String signature) {
        try {
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, keySecret);
            if (!isValid) {
                System.err.println("Invalid webhook signature");
                return;
            }

            JSONObject jsonPayload = new JSONObject(payload);
            String event = jsonPayload.getString("event");

            if ("payment.captured".equals(event)) {
                JSONObject paymentObject = jsonPayload.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

                String razorpayOrderId = paymentObject.getString("order_id");
                String razorpayPaymentId = paymentObject.getString("id");

                Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                        .orElse(null);

                if (payment != null && !"SUCCESS".equals(payment.getPaymentStatus())) {
                    payment.setRazorpayPaymentId(razorpayPaymentId);
                    payment.setPaymentStatus("SUCCESS");
                    paymentRepository.save(payment);

                    com.smartcart.paymentservice.dto.NotificationEvent notifEvent = new com.smartcart.paymentservice.dto.NotificationEvent(
                            payment.getUserId(),
                            "Payment Successful",
                            "Your payment of " + payment.getAmount() + " INR for order #" + payment.getOrderId() + " was successful.",
                            "PAYMENT_SUCCESS"
                    );
                    kafkaTemplate.send(KafkaConfig.NOTIFICATION_TOPIC, notifEvent);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing webhook: " + e.getMessage());
        }
    }
}
