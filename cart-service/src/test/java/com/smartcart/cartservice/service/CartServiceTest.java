package com.smartcart.cartservice.service;

import com.smartcart.cartservice.client.ProductClient;
import com.smartcart.cartservice.entity.Cart;
import com.smartcart.cartservice.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CartService cartService;

    @Test
    void getCartByUserId_CartExists() {
        Cart cart = Cart.builder().id(1L).userId(2L).build();
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCartByUserId(2L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCartByUserId_CartDoesNotExist() {
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart result = cartService.getCartByUserId(2L);
        assertNotNull(result);
        assertEquals(2L, result.getUserId());
    }

    @Test
    void addToCart() {
        Cart cart = Cart.builder().id(1L).userId(2L).build();
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.of(cart));

        ProductClient.ProductDto product = new ProductClient.ProductDto();
        product.setId(3L);
        product.setName("Laptop");
        product.setPrice(100.0);
        product.setStockQuantity(10);

        when(productClient.getProductById(3L)).thenReturn(product);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.addItemToCart(2L, 3L, 2);

        assertNotNull(result);
        verify(productClient).getProductById(3L);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void removeItemFromCart() {
        Cart cart = Cart.builder().id(1L).userId(2L).build();
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.removeItemFromCart(2L, 3L);

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void clearCart() {
        Cart cart = Cart.builder().id(1L).userId(2L).build();
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.of(cart));

        cartService.clearCart(2L);

        verify(cartRepository).save(any(Cart.class));
    }
}
