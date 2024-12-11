package com.quangduy.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private Long orderId;
    private PaymentStatus status;
    private String message;
}
