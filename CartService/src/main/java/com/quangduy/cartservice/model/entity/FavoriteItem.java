package com.quangduy.cartservice.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favorite_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"favorite_id", "product_id"})
})
@Getter
@Setter
public class FavoriteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many items belong to one favorite list
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_id", nullable = false)
    private Favorite favorite;

    @Column(name = "product_id", nullable = false)
    private Long productId;
}
