package com.smartcart.wishlistservice.controller;

import com.smartcart.wishlistservice.entity.Wishlist;
import com.smartcart.wishlistservice.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
public class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    private Wishlist wishlist;

    @BeforeEach
    void setUp() {
        wishlist = Wishlist.builder()
                .id(1L)
                .userId(1L)
                .productId(101L)
                .build();
    }

    @Test
    void addToWishlist_ShouldReturnOk() throws Exception {
        when(wishlistService.addToWishlist(anyLong(), anyLong())).thenReturn(wishlist);

        mockMvc.perform(post("/api/wishlist/1/add")
                .param("productId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(101));
    }

    @Test
    void removeFromWishlist_ShouldReturnNoContent() throws Exception {
        doNothing().when(wishlistService).removeFromWishlist(1L, 101L);

        mockMvc.perform(delete("/api/wishlist/1/remove/101"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserWishlist_ShouldReturnList() throws Exception {
        when(wishlistService.getUserWishlist(1L)).thenReturn(Arrays.asList(wishlist));

        mockMvc.perform(get("/api/wishlist/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(101));
    }
}
