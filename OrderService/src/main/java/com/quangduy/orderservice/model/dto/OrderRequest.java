package com.quangduy.orderservice.model.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @Nonnull
    private PaymentMethod paymentMethod;
    @NotBlank
    private String address;
}
