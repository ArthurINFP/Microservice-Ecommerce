package com.quangduy.cartservice.repository;


import com.quangduy.cartservice.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}

