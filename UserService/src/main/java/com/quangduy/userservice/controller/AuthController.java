package com.quangduy.userservice.controller;

import com.quangduy.userservice.dto.LoginRequest;
import com.quangduy.userservice.dto.SignupRequest;
import com.quangduy.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(
            @Valid @RequestBody SignupRequest signUpRequest,
            @RequestParam(required = false,defaultValue = "false") boolean isVendor) {
        userService.registerUser(signUpRequest,isVendor);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {
        String token = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(token);
    }


}

