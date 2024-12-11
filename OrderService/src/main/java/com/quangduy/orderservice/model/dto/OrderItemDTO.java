package com.quangduy.orderservice.model.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private String imgUrl;
    private int quantity;
    private Double priceUnit;
    private Double totalPrice;
}
