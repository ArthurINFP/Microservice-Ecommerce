package com.quangduy.cartservice.controller;


import com.quangduy.cartservice.model.dto.FavoriteResponse;
import com.quangduy.cartservice.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> addItemToFavorite(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long productId) {

        favoriteService.addItemToFavorite(userId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeItemFromFavorite(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long productId) {

        favoriteService.removeItemFromFavorite(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<FavoriteResponse> getFavorite(
            @RequestHeader("X-User-ID") Long userId) {

        FavoriteResponse response = favoriteService.getFavorite(userId);
        return ResponseEntity.ok(response);
    }
}
