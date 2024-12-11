package com.quangduy.paymentservice.repository;

import com.quangduy.paymentservice.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}

