package com.quangduy.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReservationResponse {
    private Long reservationId;
    private Long vendorId;
}
