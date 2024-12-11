package com.quangduy.cartservice.model.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class AddItemRequest {

    @NotNull
    private Long productId;

    @Min(1)
    private int quantity;
}
