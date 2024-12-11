package com.quangduy.cartservice.model.dto;


import lombok.Data;

import java.util.List;

@Data
public class FavoriteResponse {

    private List<FavoriteItemResponse> items;

    @Data
    public static class FavoriteItemResponse {
        private Long productId;
        private String productName;
        private String imageUrl;
        private Double priceUnit;
        private Double rating;
        private Integer reviewCount;
    }
}
