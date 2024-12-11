package com.quangduy.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveProductRequest {
    private Long productId;
    private Long userId;
    private int quantity;
}
