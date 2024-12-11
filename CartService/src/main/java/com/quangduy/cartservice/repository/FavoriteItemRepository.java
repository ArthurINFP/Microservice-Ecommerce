package com.quangduy.cartservice.repository;


import com.quangduy.cartservice.model.entity.FavoriteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {
    // Optional: Add methods if needed
}
