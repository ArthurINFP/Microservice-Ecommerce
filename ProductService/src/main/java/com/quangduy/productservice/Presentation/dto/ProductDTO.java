package com.quangduy.productservice.Presentation.dto;

import com.quangduy.productservice.Business.Domain.Category;
import com.quangduy.productservice.Business.Domain.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;

@Data
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Image URL is required")
    @URL(message = "Image URL must be a valid URL")
    private String imageUrl;

    @NotNull(message = "Price unit is required")
    @Positive(message = "Price unit must be positive")
    private Double priceUnit;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Description is required")
    private String description;


    private Long vendorId;

    public Product convertToProduct(Long vendorId) {
        return new Product(id, productName,description ,imageUrl, priceUnit, sku, quantity, category, vendorId);
    }
}
