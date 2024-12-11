package com.quangduy.paymentservice.model.dto;


import com.quangduy.paymentservice.model.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Order ID cannot be null")
    private Long orderId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod;

    // Additional fields like card details can be added here
}
