package com.quangduy.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private List<CartItemResponse> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItemResponse {
        private Long productId;
        private String productName;
        private int quantity;
        private String imageUrl;
        private double priceUnit;
        private double subtotal; // priceAtAddition × quantity
        private Long reservationId;
        private Long vendorId;
    }
    public double getTotalAmount(){
        double amount = 0;
        for (CartItemResponse cartItemResponse : items) {
            amount += cartItemResponse.getQuantity() * cartItemResponse.getPriceUnit();
        }
        return amount;
    }
}