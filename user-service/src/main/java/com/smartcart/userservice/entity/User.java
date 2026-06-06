package com.smartcart.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; 
    
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_new_user", nullable = false)
    @Builder.Default
    private boolean isNewUser = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean blocked = false;

    private String otp;

    private Date otpExpiry;
}
