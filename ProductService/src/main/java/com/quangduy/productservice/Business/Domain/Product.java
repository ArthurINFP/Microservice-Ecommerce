package com.quangduy.productservice.Business.Domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product", indexes = {
        @Index(name = "idx_product_category_id", columnList = "category_id")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "product_name", nullable = false)
    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Image URL is required")
    @URL(message = "Image URL must be a valid URL")
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @NotNull(message = "Price unit is required")
    @Positive(message = "Price unit must be positive")
    @Column(name = "price_unit", nullable = false)
    private Double priceUnit;

    @NotBlank(message = "SKU is required")
    @Column(name = "sku", unique = true, nullable = false)
    private String sku;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private Category category;

    @NotNull
    @Column(name = "vendor_id",updatable = false)
    private Long vendorId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rating", nullable = false)
    private Double rating = 0.0;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    public Product(Long id, String productName,String description ,String imageUrl, Double priceUnit, String sku, Integer quantity, Category category, Long vendorId) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.priceUnit = priceUnit;
        this.sku = sku;
        this.quantity = quantity;
        this.category = category;
        this.vendorId = vendorId;
    }
}
