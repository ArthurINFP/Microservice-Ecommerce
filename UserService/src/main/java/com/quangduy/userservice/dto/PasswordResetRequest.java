package com.quangduy.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class PasswordResetRequest {

    @NotBlank
    @Email
    private String email;
}
