package com.quangduy.cartservice.model.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class UpdateItemRequest {

    @Min(1)
    private int quantity;
}
