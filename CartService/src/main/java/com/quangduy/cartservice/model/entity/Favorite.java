package com.quangduy.cartservice.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "favorite")
@Getter
@Setter
public class Favorite {

    @Id
    private Long userId; // User ID acts as the primary key

    // One user can have many favorite items
    @OneToMany(mappedBy = "favorite", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<FavoriteItem> items = new HashSet<>();
}
