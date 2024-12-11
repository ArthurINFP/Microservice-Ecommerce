package com.quangduy.cartservice.service;

import com.quangduy.cartservice.model.dto.ProductResponse;
import com.quangduy.cartservice.model.dto.FavoriteResponse;
import com.quangduy.cartservice.model.entity.Favorite;
import com.quangduy.cartservice.model.entity.FavoriteItem;
import com.quangduy.cartservice.repository.FavoriteRepository;
import com.quangduy.cartservice.repository.FavoriteItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteItemRepository favoriteItemRepository;
    private final RestTemplate restTemplate;

    public FavoriteService(FavoriteRepository favoriteRepository, FavoriteItemRepository favoriteItemRepository, RestTemplate restTemplate) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteItemRepository = favoriteItemRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void addItemToFavorite(Long userId, Long productId) {
        Favorite favorite = favoriteRepository.findById(userId).orElseGet(() -> {
            Favorite newFavorite = new Favorite();
            newFavorite.setUserId(userId);
            return newFavorite;
        });

        boolean itemExists = favorite.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(productId));

        if (itemExists) {
            // Item already exists in favorites; do nothing
            return;
        }

        // Validate product exists
        ProductResponse productResponse = fetchProductResponse(productId);

        FavoriteItem newItem = new FavoriteItem();
        newItem.setFavorite(favorite);
        newItem.setProductId(productId);

        favorite.getItems().add(newItem);

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeItemFromFavorite(Long userId, Long productId) {
        Favorite favorite = favoriteRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Favorite list not found"));

        favorite.getItems().removeIf(item -> item.getProductId().equals(productId));

        favoriteRepository.save(favorite);
    }

    public FavoriteResponse getFavorite(Long userId) {
        Favorite favorite = favoriteRepository.findById(userId)
                .orElseGet(() -> {
                    Favorite newFavorite = new Favorite();
                    newFavorite.setUserId(userId);
                    return newFavorite;
                });

        FavoriteResponse response = new FavoriteResponse();
        List<FavoriteResponse.FavoriteItemResponse> items = new ArrayList<>();

        for (FavoriteItem item : favorite.getItems()) {
            ProductResponse productResponse = fetchProductResponse(item.getProductId());
            FavoriteResponse.FavoriteItemResponse itemResponse = new FavoriteResponse.FavoriteItemResponse();
            itemResponse.setProductId(productResponse.getId());
            itemResponse.setProductName(productResponse.getProductName());
            itemResponse.setImageUrl(productResponse.getImageUrl());
            itemResponse.setPriceUnit(productResponse.getPriceUnit());
            itemResponse.setRating(productResponse.getRating());
            itemResponse.setReviewCount(productResponse.getReviewCount());
            items.add(itemResponse);
        }

        response.setItems(items);

        return response;
    }

    // Fetch product details from Product Service
    public ProductResponse fetchProductResponse(Long productId) {
        String url = "http://productservice/product/get/id/" + productId;

        ResponseEntity<ProductResponse> response = restTemplate.getForEntity(url, ProductResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Product not found or service unavailable");
        }

        return response.getBody();
    }
}
