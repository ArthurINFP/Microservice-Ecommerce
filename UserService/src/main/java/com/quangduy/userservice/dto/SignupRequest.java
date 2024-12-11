package com.quangduy.userservice.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class SignupRequest {

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Full name must not be blank")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName;

    @NotBlank(message = "Address must not be blank")
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\+84[0-9]{9,10}$|^0[0-9]{9,10}$", message = "The phone number is not in the correct format")
    @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 characters")
    private String phone;

    private String imageUrl;

    @NotBlank
    @Size(min = 8)
    private String password;
}
