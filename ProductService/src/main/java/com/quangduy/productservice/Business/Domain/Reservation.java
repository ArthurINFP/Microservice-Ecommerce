package com.quangduy.productservice.Business.Domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private Long productId;

    private Long userId;

    private int quantity;

    private Long orderId; // Optional, if associated with an order

    // Additional fields like timestamp can be added
}
