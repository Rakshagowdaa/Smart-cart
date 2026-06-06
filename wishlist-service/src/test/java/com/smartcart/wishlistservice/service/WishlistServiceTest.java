package com.smartcart.wishlistservice.service;

import com.smartcart.wishlistservice.entity.Wishlist;
import com.smartcart.wishlistservice.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
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
    void addToWishlist_WhenNew_ShouldReturnSaved() {
        when(wishlistRepository.findByUserIdAndProductId(1L, 101L)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        Wishlist saved = wishlistService.addToWishlist(1L, 101L);

        assertNotNull(saved);
        assertEquals(101L, saved.getProductId());
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
    }

    @Test
    void addToWishlist_WhenExists_ShouldThrowException() {
        when(wishlistRepository.findByUserIdAndProductId(1L, 101L)).thenReturn(Optional.of(wishlist));

        assertThrows(RuntimeException.class, () -> wishlistService.addToWishlist(1L, 101L));
    }

    @Test
    void removeFromWishlist_WhenExists_ShouldDelete() {
        when(wishlistRepository.findByUserIdAndProductId(1L, 101L)).thenReturn(Optional.of(wishlist));

        wishlistService.removeFromWishlist(1L, 101L);

        verify(wishlistRepository, times(1)).delete(wishlist);
    }

    @Test
    void removeFromWishlist_WhenNotExists_ShouldThrowException() {
        when(wishlistRepository.findByUserIdAndProductId(1L, 101L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> wishlistService.removeFromWishlist(1L, 101L));
    }

    @Test
    void getUserWishlist_ShouldReturnList() {
        when(wishlistRepository.findByUserId(1L)).thenReturn(Arrays.asList(wishlist));

        List<Wishlist> list = wishlistService.getUserWishlist(1L);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }
}
