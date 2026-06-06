package com.smartcart.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcart.productservice.entity.Review;
import com.smartcart.productservice.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addReview() throws Exception {
        Review review = Review.builder().rating(5).comment("Great").build();
        Review savedReview = Review.builder().id(1L).rating(5).comment("Great").productId(1L).build();

        when(reviewService.addReview(any(Review.class))).thenReturn(savedReview);

        mockMvc.perform(post("/api/products/1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getReviewsForProduct() throws Exception {
        Review review = Review.builder().id(1L).rating(4).comment("Good").build();
        when(reviewService.getReviewsForProduct(1L)).thenReturn(Arrays.asList(review));

        mockMvc.perform(get("/api/products/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(4));
    }
}
