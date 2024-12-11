package com.quangduy.productservice.Presentation.dto;


import com.quangduy.productservice.Business.Domain.Review;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private Long userId;
    private String userName;
    private String userImageUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.userId = review.getUserId();
        this.userName = review.getUserName();
        this.userImageUrl = review.getUserImageUrl();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }
}

