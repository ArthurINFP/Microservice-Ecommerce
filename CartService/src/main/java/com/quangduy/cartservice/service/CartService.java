package com.quangduy.cartservice.service;



import com.quangduy.cartservice.model.dto.*;
import com.quangduy.cartservice.model.entity.Cart;
import com.quangduy.cartservice.model.entity.CartItem;
import com.quangduy.cartservice.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RestTemplate restTemplate; // For Product Service communication

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, RestTemplate restTemplate) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void addItemToCart(Long userId, AddItemRequest request) {
        Cart cart = cartRepository.findById(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return newCart;
        });

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();
        // Check if product exist by Id
        ProductResponse productResponse = fetchProductDTO(request.getProductId());

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());

        } else {
            // Add new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());

            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    @Transactional
    public void updateItemQuantity(Long userId, Long productId, UpdateItemRequest request) {
        Cart cart = cartRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        item.setQuantity(request.getQuantity());

        cartRepository.save(cart);
    }

    @Transactional
    public void removeItemFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        cartRepository.save(cart);
    }

    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findById(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return newCart;
                });

        CartResponse response = new CartResponse();
        List<CartResponse.CartItemResponse> items = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            ProductResponse productResponse = fetchProductDTO(item.getProductId());
            CartResponse.CartItemResponse itemResponse = new CartResponse.CartItemResponse();
            itemResponse.setProductId(productResponse.getId());
            itemResponse.setProductName(productResponse.getProductName());
            itemResponse.setImageUrl(productResponse.getImageUrl());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPriceUnit(productResponse.getPriceUnit());
            itemResponse.setSubtotal(productResponse.getPriceUnit() * item.getQuantity());
            items.add(itemResponse);
        }

        response.setItems(items);

        return response;
    }

    @Transactional
    public void updateCart(Long userId, UpdateCartRequest request) {
        // Fetch or create the cart
        Cart cart = cartRepository.findById(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return newCart;
        });

        // Clear existing items
        cart.getItems().clear();
        cartRepository.save(cart);
        cartRepository.flush();
        // Add new items
        for (UpdateCartRequest.CartItemRequest itemRequest : request.getItems()) {
            // Validate product exists
            ProductResponse productResponse = fetchProductDTO(itemRequest.getProductId());

            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductId(itemRequest.getProductId());
            newItem.setQuantity(itemRequest.getQuantity());

            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteById(userId);
        cartRepository.flush();
    }

    //
    public ProductResponse fetchProductDTO(Long productId) {
        // The URL should match the actual endpoint path
        String url = "http://productservice/product/get/id/" + productId;

        // Using RestTemplate to send the GET request
        ResponseEntity<ProductResponse> response = restTemplate.getForEntity(url, ProductResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Product not found or service unavailable");
        }

        ProductResponse product = response.getBody();

        // Return the price as a double
        return product;
    }

}
