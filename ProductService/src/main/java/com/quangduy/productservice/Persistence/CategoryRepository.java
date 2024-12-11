package com.quangduy.productservice.Persistence;

import com.quangduy.productservice.Business.Domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Additional custom query methods can be defined here if needed
    Optional<Category> findByCategoryName(String name);
    Optional<Category> findByCategoryId(Integer id);
}
