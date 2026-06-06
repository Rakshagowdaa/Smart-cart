package com.smartcart.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String phoneNumber;
    private boolean isNewUser;
    private String otp;
    private java.util.Date otpExpiry;
}
