package com.smartcart.productservice.service;

import com.smartcart.productservice.entity.Review;
import com.smartcart.productservice.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void addReview_Success() {
        Review review = Review.builder().rating(5).build();
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.addReview(review);
        assertEquals(5, result.getRating());
    }

    @Test
    void addReview_InvalidRating() {
        Review review = Review.builder().rating(6).build();
        assertThrows(RuntimeException.class, () -> reviewService.addReview(review));
    }

    @Test
    void getReviewsForProduct() {
        Review review = Review.builder().id(1L).build();
        when(reviewRepository.findByProductId(1L)).thenReturn(Arrays.asList(review));

        List<Review> result = reviewService.getReviewsForProduct(1L);
        assertEquals(1, result.size());
    }
}
