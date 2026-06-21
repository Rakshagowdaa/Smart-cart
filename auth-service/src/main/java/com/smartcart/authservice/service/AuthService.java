package com.smartcart.authservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import com.smartcart.authservice.dto.NotificationEvent;
import com.smartcart.authservice.config.KafkaConfig;
import com.smartcart.authservice.client.UserClient;
import com.smartcart.authservice.dto.AuthRequest;
import com.smartcart.authservice.dto.AuthResponse;
import com.smartcart.authservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        UserDto user = userClient.getUserByEmail(request.getEmail());
        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = jwtService.generateToken(user.getEmail(), user.getRole());
            user.setPassword(null); 
            return new AuthResponse(token, user);
        }
        throw new RuntimeException("Invalid credentials");
    }

    public AuthResponse register(UserDto userDto) {
        System.out.println("BACKEND DEBUG: Received registration request for email: " + userDto.getEmail() + " with role: " + userDto.getRole());
        
        String requestedRole = userDto.getRole();
        if (requestedRole == null || requestedRole.isEmpty()) {
            requestedRole = "USER";
        }
        
        // Security check: Only an authenticated ADMIN can register non-USER accounts (VENDOR or ADMIN)
        if (!"USER".equals(requestedRole)) {
            boolean isAdmin = false;
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    try {
                        String token = authHeader.substring(7);
                        Claims claims = jwtService.validateToken(token);
                        String requesterRole = claims.get("role", String.class);
                        if ("ADMIN".equals(requesterRole)) {
                            isAdmin = true;
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to validate token for elevated role registration: " + e.getMessage());
                    }
                }
            }
            if (!isAdmin) {
                System.out.println("SECURITY WARNING: Non-admin attempted to register role: " + requestedRole + ". Forcing to USER.");
                requestedRole = "USER";
            }
        }
        
        userDto.setRole(requestedRole);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        
        System.out.println("BACKEND DEBUG: Sending to User-Service with role: " + userDto.getRole());
        UserDto createdUser = userClient.createUser(userDto);
        System.out.println("BACKEND DEBUG: User-Service created user with role: " + createdUser.getRole());
        String token = jwtService.generateToken(createdUser.getEmail(), createdUser.getRole());
        createdUser.setPassword(null);
        
        
        try {
            NotificationEvent event = new NotificationEvent(
                createdUser.getId(),
                "Welcome to Smart Cart!",
                "Hello " + createdUser.getName() + ", your account has been created successfully. Use WELCOME10 for your first order discount!",
                "WELCOME"
            );
            kafkaTemplate.send(KafkaConfig.NOTIFICATION_TOPIC, event);
        } catch (Exception e) {
           
            System.err.println("Failed to send welcome notification: " + e.getMessage());
        }

        return new AuthResponse(token, createdUser);
    }

    public void forgotPassword(String email) {
        UserDto user = userClient.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        long expiryTime = System.currentTimeMillis() + 10 * 60 * 1000; 

        userClient.updateOtp(user.getId(), otp, expiryTime);

        try {
            NotificationEvent event = new NotificationEvent(
                user.getId(),
                "Password Reset OTP",
                "Your OTP for password reset is: " + otp + ". It is valid for 10 minutes.",
                "OTP"
            );
            kafkaTemplate.send(KafkaConfig.NOTIFICATION_TOPIC, event);
        } catch (Exception e) {
            System.err.println("Failed to send OTP notification: " + e.getMessage());
        }
    }

    public void resetPassword(String email, String otp, String newPassword) {
        UserDto user = userClient.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().getTime() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP has expired");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        userClient.updatePassword(user.getId(), encodedPassword);
    }
}
