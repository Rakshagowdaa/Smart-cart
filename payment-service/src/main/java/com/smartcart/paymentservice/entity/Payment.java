package com.smartcart.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long userId;

    private Double amount;
    private String currency; 

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private String paymentStatus; 
    
    private Date createdAt;
}
