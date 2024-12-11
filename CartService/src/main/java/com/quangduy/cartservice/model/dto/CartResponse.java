package com.quangduy.cartservice.model.dto;


import lombok.*;
import java.util.List;

@Data
public class CartResponse {

    private List<CartItemResponse> items;

    @Data
    public static class CartItemResponse {
        private Long productId;
        private String productName;
        private int quantity;
        private String imageUrl;
        private double priceUnit;
        private double subtotal; // priceAtAddition × quantity
    }
}
