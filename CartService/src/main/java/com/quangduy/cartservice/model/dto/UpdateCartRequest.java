package com.quangduy.cartservice.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class UpdateCartRequest {

    @NotEmpty(message = "Cart items cannot be empty")
    private List<CartItemRequest> items;

    @Data
    public static class CartItemRequest {
        @NotNull(message = "Product ID cannot be null")
        private Long productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
    }
}
