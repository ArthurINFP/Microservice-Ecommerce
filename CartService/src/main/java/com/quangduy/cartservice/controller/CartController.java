package com.quangduy.cartservice.controller;


import com.quangduy.cartservice.exception.CartException;
import com.quangduy.cartservice.model.dto.AddItemRequest;
import com.quangduy.cartservice.model.dto.CartResponse;
import com.quangduy.cartservice.model.dto.UpdateCartRequest;
import com.quangduy.cartservice.model.dto.UpdateItemRequest;
import com.quangduy.cartservice.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItemToCart(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Roles") String roles,
            @Valid @RequestBody AddItemRequest request) {

        authorizeUserRole(roles);

        cartService.addItemToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Void> updateItemQuantity(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Roles") String roles,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateItemRequest request) {

        authorizeUserRole(roles);

        cartService.updateItemQuantity(userId, productId, request);
        return ResponseEntity.ok().build();
    }
    @PutMapping
    public ResponseEntity<Void> updateCart(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Roles") String roles,
            @Valid @RequestBody UpdateCartRequest request) {

        authorizeUserRole(roles);

        cartService.updateCart(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Roles") String roles,
            @PathVariable Long productId) {

        authorizeUserRole(roles);

        cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Roles") String roles) {

        authorizeUserRole(roles);

        CartResponse cartResponse = cartService.getCart(userId);
        return ResponseEntity.ok(cartResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Roles") String roles) {

        authorizeUserRole(roles);

        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    private void authorizeUserRole(String roles) {
        if (!roles.contains("ROLE_USER")) {
            throw new CartException.UnauthorizedException("Access denied");
        }
    }
}
