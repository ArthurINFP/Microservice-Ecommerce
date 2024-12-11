package com.quangduy.productservice.Presentation.dto;

public enum QuantityFilter {
    OUT_OF_STOCK,   // Quantity ≤ 0
    LOW_STOCK,      // Quantity < 10
    IN_STOCK // Quantity ≥ 10
}