package com.smartcart.apigateway.filter;

import com.smartcart.apigateway.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain chain;

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        authenticationFilter = new AuthenticationFilter();
        
        org.springframework.test.util.ReflectionTestUtils.setField(authenticationFilter, "jwtUtil", jwtUtil);
    }

    @Test
    void filter_WhenAuthPath_ShouldProceed() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/auth/login")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        authenticationFilter.apply(new AuthenticationFilter.Config()).filter(exchange, chain);

        verify(chain, times(1)).filter(exchange);
    }

    @Test
    void filter_WhenNoAuthHeader_ShouldReturnUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/products")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        authenticationFilter.apply(new AuthenticationFilter.Config()).filter(exchange, chain);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_WhenValidToken_ShouldProceed() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.getRole("valid-token")).thenReturn("USER");
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        authenticationFilter.apply(new AuthenticationFilter.Config()).filter(exchange, chain);

        verify(chain, times(1)).filter(exchange);
    }

    @Test
    void filter_WhenAdminRequiredAndUserRole_ShouldReturnForbidden() {
        MockServerHttpRequest request = MockServerHttpRequest
                .post("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.getRole("user-token")).thenReturn("USER");

        authenticationFilter.apply(new AuthenticationFilter.Config()).filter(exchange, chain);

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_WhenAdminRequiredAndAdminRole_ShouldProceed() {
        MockServerHttpRequest request = MockServerHttpRequest
                .post("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.getRole("admin-token")).thenReturn("ADMIN");
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        authenticationFilter.apply(new AuthenticationFilter.Config()).filter(exchange, chain);

        verify(chain, times(1)).filter(exchange);
    }

    @Test
    void filter_WhenInvalidToken_ShouldReturnForbidden() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        doThrow(new RuntimeException("Invalid token")).when(jwtUtil).validateToken("invalid-token");

        authenticationFilter.apply(new AuthenticationFilter.Config()).filter(exchange, chain);

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
    }
}
