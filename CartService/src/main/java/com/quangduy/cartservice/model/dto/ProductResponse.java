package com.quangduy.cartservice.model.dto;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String productName;
    private String imageUrl;
    private Double priceUnit;
    private String sku;
    private Integer quantity;
    private String description;
    private Double rating;
    private Integer reviewCount;
    private Long vendorId;
}
