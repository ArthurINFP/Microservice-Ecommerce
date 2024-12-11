package com.quangduy.productservice.Presentation.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReserveProductRequest {
    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
