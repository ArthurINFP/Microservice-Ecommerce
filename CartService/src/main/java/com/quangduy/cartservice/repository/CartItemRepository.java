package com.quangduy.cartservice.repository;


import com.quangduy.cartservice.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}

