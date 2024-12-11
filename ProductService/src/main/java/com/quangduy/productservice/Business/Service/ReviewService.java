package com.quangduy.productservice.Business.Service;



import com.quangduy.productservice.Business.Domain.Product;
import com.quangduy.productservice.Business.Domain.Review;
import com.quangduy.productservice.Persistence.ProductRepository;
import com.quangduy.productservice.Persistence.ReviewRepository;
import com.quangduy.productservice.Presentation.dto.UserProfileResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository, RestTemplate restTemplate) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public Review addReview(Long productId, Long userId, Integer rating, String comment) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Check if user has already reviewed this product
        Optional<Review> existingReview = reviewRepository.findByProductIdAndUserId(productId, userId);
        if (existingReview.isPresent()) {
            throw new IllegalStateException("User has already reviewed this product");
        }
        UserProfileResponse userProfileResponse = getUserInfor(userId);

        Review review = new Review();
        review.setProduct(product);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment);
        review.setUserImageUrl(userProfileResponse.getImageUrl());
        review.setUserName(userProfileResponse.getFullName());

        Review savedReview = reviewRepository.save(review);

        // Update product rating and review count
        updateProductRatingAndReviewCount(product);

        return savedReview;
    }

    @Transactional
    public Review updateReview(Long reviewId, Long userId, Integer rating, String comment) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new IllegalStateException("User is not authorized to update this review");
        }
        UserProfileResponse userProfileResponse = getUserInfor(userId);

        review.setRating(rating);
        review.setComment(comment);
        review.setUserName(userProfileResponse.getFullName());
        review.setUserImageUrl(userProfileResponse.getImageUrl());

        Review updatedReview = reviewRepository.save(review);

        // Update product rating
        updateProductRatingAndReviewCount(review.getProduct());

        return updatedReview;
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new IllegalStateException("User is not authorized to delete this review");
        }

        Product product = review.getProduct();

        reviewRepository.delete(review);

        // Update product rating and review count
        updateProductRatingAndReviewCount(product);
    }

    public Page<Review> getReviewsByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable);
    }

    private void updateProductRatingAndReviewCount(Product product) {
        // Calculate average rating
        Double averageRating = reviewRepository.calculateAverageRatingByProductId(product.getId());
        if (averageRating == null) {
            averageRating = 0.0;
        }

        // Count number of reviews
        long reviewCount = reviewRepository.countByProductId(product.getId());

        // Update product
        product.setRating(averageRating);
        product.setReviewCount((int) reviewCount);

        productRepository.save(product);
    }

    public UserProfileResponse getUserInfor(Long userId){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-ID", userId.toString());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String cartUrl = "http://userservice/profile/me";
        ResponseEntity<UserProfileResponse> response = restTemplate.exchange(cartUrl, HttpMethod.GET, entity, UserProfileResponse.class);
        UserProfileResponse userProfileResponse = response.getBody();
        if (userProfileResponse == null) {
            throw new IllegalArgumentException("Cant find user profile");
        }
        return userProfileResponse;
    }
}

