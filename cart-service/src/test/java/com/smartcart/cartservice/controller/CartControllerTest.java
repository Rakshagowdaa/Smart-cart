package com.smartcart.cartservice.controller;


import com.smartcart.cartservice.entity.Cart;
import com.smartcart.cartservice.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Test
    void addToCart() throws Exception {
        Cart cart = Cart.builder().id(1L).userId(2L).build();
        when(cartService.addItemToCart(2L, 3L, 1)).thenReturn(cart);

        mockMvc.perform(post("/api/cart/2/add")
                .param("productId", "3")
                .param("quantity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getCart() throws Exception {
        Cart cart = Cart.builder().id(1L).userId(2L).totalPrice(100.0).build();
        when(cartService.getCartByUserId(2L)).thenReturn(cart);

        mockMvc.perform(get("/api/cart/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(100.0));
    }

    @Test
    void clearCart() throws Exception {
        mockMvc.perform(delete("/api/cart/2/clear"))
                .andExpect(status().isOk());
    }
}
