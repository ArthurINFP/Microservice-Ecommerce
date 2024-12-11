package com.quangduy.productservice.Presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReleaseProductRequest {
    @NotNull(message = "Reservation ID cannot be null")
    private Long reservationId;
}
