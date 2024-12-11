package com.quangduy.orderservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key to Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore // Prevent infinite recursion
    private Order order;

    private Long productId;
    private String productName;
    private String imgUrl;

    private int quantity;
    private Long vendorId;

    private Double priceUnit;
    private Long reservationId;
}

