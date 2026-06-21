package com.smartcart.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcart.authservice.dto.AuthRequest;
import com.smartcart.authservice.dto.AuthResponse;
import com.smartcart.authservice.dto.UserDto;
import com.smartcart.authservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_Success() throws Exception {
        AuthRequest request = new AuthRequest("john@example.com", "password");
        UserDto userDto = UserDto.builder().id(1L).email("john@example.com").build();
        AuthResponse response = new AuthResponse("token", userDto);

        when(authService.login(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));
    }

    @Test
    void register_Success() throws Exception {
        UserDto request = UserDto.builder().email("john@example.com").password("password").build();
        UserDto userDto = UserDto.builder().id(1L).email("john@example.com").build();
        AuthResponse response = new AuthResponse("token", userDto);

        when(authService.register(any(UserDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));
    }

    @Test
    void forgotPassword_Success() throws Exception {
        doNothing().when(authService).forgotPassword("john@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                .param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP sent to your email."));
    }

    @Test
    void forgotPassword_UserNotFound_Returns400() throws Exception {
        doThrow(new RuntimeException("User not found"))
                .when(authService).forgotPassword("unknown@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                .param("email", "unknown@example.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_Success() throws Exception {
        doNothing().when(authService).resetPassword(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/auth/reset-password")
                .param("email", "john@example.com")
                .param("otp", "123456")
                .param("newPassword", "newpass123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully."));
    }

    @Test
    void resetPassword_InvalidOtp_Returns400() throws Exception {
        doThrow(new RuntimeException("Invalid OTP"))
                .when(authService).resetPassword(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/auth/reset-password")
                .param("email", "john@example.com")
                .param("otp", "wrong")
                .param("newPassword", "newpass123"))
                .andExpect(status().isBadRequest());
    }
}
