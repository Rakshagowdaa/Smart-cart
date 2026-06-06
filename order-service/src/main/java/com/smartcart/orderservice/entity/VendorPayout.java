package com.smartcart.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "vendor_payouts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPayout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vendorId;
    private Long orderId;
    private Double amount;
    private String status; // PENDING, PAID
    private Date createdAt;
}
