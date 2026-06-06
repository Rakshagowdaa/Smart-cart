package com.smartcart.productservice.controller;

import com.smartcart.productservice.entity.Review;
import com.smartcart.productservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(@PathVariable Long productId, @RequestBody Review review) {
        review.setProductId(productId);
        return new ResponseEntity<>(reviewService.addReview(review), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviewsForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsForProduct(productId));
    }
}
