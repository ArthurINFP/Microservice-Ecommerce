package com.quangduy.productservice.Presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReservationResponse {
    private Long reservationId;
    private Long vendorId;
}
