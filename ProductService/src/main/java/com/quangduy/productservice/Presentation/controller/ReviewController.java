package com.quangduy.productservice.Presentation.controller;



import com.quangduy.productservice.Business.Domain.Review;
import com.quangduy.productservice.Business.Service.ReviewService;
import com.quangduy.productservice.Presentation.dto.CreateReviewRequest;
import com.quangduy.productservice.Presentation.dto.ReviewDTO;
import com.quangduy.productservice.Presentation.dto.UpdateReviewRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews/{productId}")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> addReview(
            @PathVariable Long productId,
            @RequestHeader("X-User-ID") Long userId,
            @Valid @RequestBody CreateReviewRequest request) {

        Review review = reviewService.addReview(productId, userId, request.getRating(), request.getComment());

        ReviewDTO response = new ReviewDTO(review);

        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @RequestHeader("X-User-ID") Long userId,
            @Valid @RequestBody UpdateReviewRequest request) {

        Review updatedReview = reviewService.updateReview(reviewId, userId, request.getRating(), request.getComment());

        ReviewDTO response = new ReviewDTO(updatedReview);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @RequestHeader("X-User-ID") Long userId) {

        reviewService.deleteReview(reviewId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReviewDTO>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewsPage = reviewService.getReviewsByProductId(productId, pageable);

        List<ReviewDTO> reviewDTOs = reviewsPage.getContent().stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());

        Page<ReviewDTO> responsePage = new PageImpl<>(reviewDTOs, pageable, reviewsPage.getTotalElements());

        return ResponseEntity.ok(responsePage);
    }
}
