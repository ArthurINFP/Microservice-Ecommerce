package com.quangduy.productservice.Presentation.dto;

import com.quangduy.productservice.Business.Domain.Category;
import com.quangduy.productservice.Business.Domain.Product;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String productName;
    private String imageUrl;
    private Double priceUnit;
    private String sku;
    private Integer quantity;
    private String description;
    private Double rating;
    private Category category;
    private Integer reviewCount;
    private Long vendorId;

    public static ProductResponse toDTO(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setImageUrl(product.getImageUrl());
        dto.setPriceUnit(product.getPriceUnit());
        dto.setSku(product.getSku());
        dto.setQuantity(product.getQuantity());
        dto.setDescription(product.getDescription());
        dto.setRating(product.getRating());
        dto.setCategory(product.getCategory());
        dto.setReviewCount(product.getReviewCount());
        dto.setVendorId(product.getVendorId());// We'll need to calculate or store this
        return dto;
    }
}
