package com.smartcart.authservice.service;

import com.smartcart.authservice.client.UserClient;
import com.smartcart.authservice.dto.AuthRequest;
import com.smartcart.authservice.dto.AuthResponse;
import com.smartcart.authservice.dto.UserDto;
import com.smartcart.authservice.dto.NotificationEvent;
import com.smartcart.authservice.config.KafkaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserClient userClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserDto userDto;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .password("encoded_pass")
                .role("USER")
                .build();

        authRequest = new AuthRequest("alice@example.com", "plain_pass");
    }

    @Test
    void login_Success() {
        when(userClient.getUserByEmail(authRequest.getEmail())).thenReturn(userDto);
        when(passwordEncoder.matches("plain_pass", "encoded_pass")).thenReturn(true);
        when(jwtService.generateToken("alice@example.com", "USER")).thenReturn("mock-jwt-token");

        AuthResponse response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertNull(response.getUser().getPassword());
    }

    @Test
    void login_InvalidCredentials() {
        when(userClient.getUserByEmail(authRequest.getEmail())).thenReturn(userDto);
        when(passwordEncoder.matches("plain_pass", "encoded_pass")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(authRequest));
    }

    @Test
    void register_Success_WithDefaultRole() {
        UserDto requestDto = UserDto.builder().name("Alice").email("alice@example.com").password("plain_pass").build(); // role is null
        
        when(passwordEncoder.encode("plain_pass")).thenReturn("encoded_pass");
        when(userClient.createUser(any(UserDto.class))).thenAnswer(invocation -> {
            UserDto dto = invocation.getArgument(0);
            dto.setId(1L);
            return dto;
        });
        when(jwtService.generateToken("alice@example.com", "USER")).thenReturn("mock-jwt-token");

        AuthResponse response = authService.register(requestDto);

        assertNotNull(response);
        assertEquals("USER", response.getUser().getRole());
        verify(kafkaTemplate, times(1)).send(eq(KafkaConfig.NOTIFICATION_TOPIC), any(NotificationEvent.class));
    }

    @Test
    void register_NotificationFailure_ShouldStillSucceed() {
        UserDto requestDto = UserDto.builder().name("Alice").email("alice@example.com").password("plain_pass").role("USER").build();
        
        when(passwordEncoder.encode("plain_pass")).thenReturn("encoded_pass");
        when(userClient.createUser(any(UserDto.class))).thenReturn(userDto);
        when(jwtService.generateToken("alice@example.com", "USER")).thenReturn("mock-jwt-token");
        when(kafkaTemplate.send(anyString(), any())).thenThrow(new RuntimeException("Kafka down"));

        AuthResponse response = authService.register(requestDto);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        // Should not throw exception
    }

    @Test
    void forgotPassword_Success() {
        when(userClient.getUserByEmail("alice@example.com")).thenReturn(userDto);
        when(userClient.updateOtp(anyLong(), anyString(), anyLong())).thenReturn(userDto);

        authService.forgotPassword("alice@example.com");

        verify(userClient, times(1)).updateOtp(eq(1L), anyString(), anyLong());
        verify(kafkaTemplate, times(1)).send(eq(KafkaConfig.NOTIFICATION_TOPIC), any(NotificationEvent.class));
    }

    @Test
    void forgotPassword_UserNotFound() {
        when(userClient.getUserByEmail("unknown@example.com")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authService.forgotPassword("unknown@example.com"));
    }

    @Test
    void resetPassword_Success() {
        userDto.setOtp("123456");
        userDto.setOtpExpiry(new java.util.Date(System.currentTimeMillis() + 600000));
        
        when(userClient.getUserByEmail("alice@example.com")).thenReturn(userDto);
        when(passwordEncoder.encode("new_pass")).thenReturn("encoded_new_pass");
        when(userClient.updatePassword(1L, "encoded_new_pass")).thenReturn(userDto);

        authService.resetPassword("alice@example.com", "123456", "new_pass");

        verify(userClient, times(1)).updatePassword(1L, "encoded_new_pass");
    }

    @Test
    void resetPassword_InvalidOtp() {
        userDto.setOtp("123456");
        userDto.setOtpExpiry(new java.util.Date(System.currentTimeMillis() + 600000));
        
        when(userClient.getUserByEmail("alice@example.com")).thenReturn(userDto);

        assertThrows(RuntimeException.class, () -> authService.resetPassword("alice@example.com", "wrong_otp", "new_pass"));
    }

    @Test
    void resetPassword_ExpiredOtp() {
        userDto.setOtp("123456");
        userDto.setOtpExpiry(new java.util.Date(System.currentTimeMillis() - 1000)); // Expired
        
        when(userClient.getUserByEmail("alice@example.com")).thenReturn(userDto);

        assertThrows(RuntimeException.class, () -> authService.resetPassword("alice@example.com", "123456", "new_pass"));
    }

    @Test
    void resetPassword_UserNotFound() {
        when(userClient.getUserByEmail("unknown@example.com")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authService.resetPassword("unknown@example.com", "123456", "new_pass"));
    }
}

