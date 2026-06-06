package com.smartcart.authservice.service;

import io.jsonwebtoken.Claims;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;



import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private String testSecret = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hour
    }

    @Test
    void generateToken_Success() {
        String token = jwtService.generateToken("alice@example.com", "USER");
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void validateToken_Success() {
        String token = jwtService.generateToken("alice@example.com", "USER");
        Claims claims = jwtService.validateToken(token);

        assertNotNull(claims);
        assertEquals("alice@example.com", claims.getSubject());
        assertEquals("USER", claims.get("role"));
    }

    @Test
    void validateToken_Expired() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L); // Expired 1 second ago
        String expiredToken = jwtService.generateToken("alice@example.com", "USER");

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
            jwtService.validateToken(expiredToken);
        });
    }

    @Test
    void validateToken_Malformed() {
        assertThrows(io.jsonwebtoken.MalformedJwtException.class, () -> {
            jwtService.validateToken("invalid.jwt.token");
        });
    }
}
