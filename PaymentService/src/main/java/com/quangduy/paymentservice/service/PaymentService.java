package com.quangduy.paymentservice.service;


import com.quangduy.paymentservice.model.dto.*;
import com.quangduy.paymentservice.model.entity.Payment;
import com.quangduy.paymentservice.model.entity.PaymentStatus;
import com.quangduy.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // Simulate payment processing logic
        PaymentStatus status;
        String message;

        // For simulation, let's assume all payments succeed
        status = PaymentStatus.SUCCESS;
        message = "Payment processed successfully";

        // Save payment record
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(status);
        payment.setTimestamp(LocalDateTime.now());
        payment.setMessage(message);

        Payment savedPayment = paymentRepository.save(payment);

        // Build response
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(savedPayment.getId());
        response.setOrderId(savedPayment.getOrderId());
        response.setStatus(savedPayment.getStatus());
        response.setMessage(savedPayment.getMessage());

        return response;
    }
}
