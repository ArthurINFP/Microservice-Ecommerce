package com.quangduy.userservice.controller;

import com.quangduy.userservice.dto.UpdateRequest;
import com.quangduy.userservice.dto.UserProfileResponse;
import com.quangduy.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class UserProfileControler {

    private final UserService userService;

    public UserProfileControler(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            @RequestHeader("X-User-ID") Long userId) {

        return ResponseEntity.ok(userService.getCurrentUser(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUser(
            @RequestHeader("X-User-ID") Long userId,
            @Valid @RequestBody UpdateRequest updateRequest) {

        return ResponseEntity.ok(userService.updateUserProfile(userId,updateRequest));
    }
    @PostMapping("{vendorId}")
    public ResponseEntity<UserProfileResponse> getVendorProfile(
            @PathVariable Long vendorId
    ){
        return ResponseEntity.ok(userService.getUserProfile(vendorId));
    }
}


