package com.quangduy.paymentservice.model.dto;


import com.quangduy.paymentservice.model.entity.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponse {

    private Long paymentId;
    private Long orderId;
    private PaymentStatus status;
    private String message;
}
