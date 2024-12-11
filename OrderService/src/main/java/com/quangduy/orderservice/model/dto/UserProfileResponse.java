package com.quangduy.orderservice.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String fullName;
    private String imageUrl;
    private String email;
    private String phone;
    private String address;
}
