package com.quangduy.orderservice.model.entity;


import com.quangduy.orderservice.model.dto.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String userName;
    private String phoneNumber;

    private Double totalAmount;

    private LocalDateTime orderDate;

    private OrderStatus status; // e.g., 'PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED'

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    private String address;

    private PaymentMethod paymentMethod;
}

