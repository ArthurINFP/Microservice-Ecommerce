package com.quangduy.cartservice.repository;

import com.quangduy.cartservice.model.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // No additional methods needed at this time
}

